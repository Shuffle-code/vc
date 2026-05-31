package tt.chat.vc.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class VideoProxyController {

    private final RestTemplate restTemplate = new RestTemplate();

    // Хранилище кэша: ключ = URL, значение = данные + время сохранения
    private static final ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();

    // Время жизни кэша в миллисекундах (5 минут)
    private static final long CACHE_TTL = 5 * 60 * 1000;

    @GetMapping("/video-proxy")
    public ResponseEntity<byte[]> proxyVideo(@RequestParam("url") String originalUrl) {
        try {
            // 1. Проверяем, есть ли данные в кэше и не устарели ли они
            CacheEntry cached = cache.get(originalUrl);
            if (cached != null && !cached.isExpired()) {
                System.out.println("✅ Cache HIT: " + originalUrl);
                return buildResponse(cached.getData(), originalUrl);
            }

            System.out.println("❌ Cache MISS: " + originalUrl + " (скачиваем с CDN)");

            // 2. Скачиваем с CDN (ваш сервер в Чехии)
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    URI.create(originalUrl),
                    HttpMethod.GET,
                    null,
                    byte[].class
            );

            byte[] data = response.getBody();
            if (data == null) {
                return ResponseEntity.notFound().build();
            }

            // 3. Сохраняем в кэш (только для .ts файлов, не для плейлистов)
            if (originalUrl.contains(".ts")) {
                cache.put(originalUrl, new CacheEntry(data));
                System.out.println("💾 Сохранено в кэш: " + originalUrl + " (размер: " + data.length + " байт)");
            }

            // 4. Для плейлистов (.m3u8) заменяем ссылки внутри
            if (originalUrl.contains(".m3u8")) {
                String content = new String(data, "UTF-8");
                String modified = replaceUrlsInM3u8(content);
                data = modified.getBytes("UTF-8");
                System.out.println("📝 Плейлист обработан, ссылки заменены");
            }

            // 5. Возвращаем результат
            return buildResponse(data, originalUrl);

        } catch (Exception e) {
            System.err.println("Ошибка прокси: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    // Вспомогательный метод для построения ответа
    private ResponseEntity<byte[]> buildResponse(byte[] data, String url) {
        String contentType = detectContentType(url);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
                .body(data);
    }

    // Определяем тип контента по расширению
    private String detectContentType(String url) {
        if (url.contains(".m3u8")) return "application/vnd.apple.mpegurl";
        if (url.contains(".ts")) return "video/MP2T";
        if (url.contains(".key")) return "application/octet-stream";
        return "application/octet-stream";
    }

    // Замена ссылок внутри плейлиста
    private String replaceUrlsInM3u8(String content) {
        String regex = "(https?://[^\\s\"'<>]+)\\.(ts|m3u8|key)";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        java.util.regex.Matcher matcher = pattern.matcher(content);

        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String fullUrl = matcher.group(0);
            String encoded = java.net.URLEncoder.encode(fullUrl, java.nio.charset.StandardCharsets.UTF_8);
            matcher.appendReplacement(sb, "/video-proxy?url=" + encoded);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    // Очистка кэша (каждую минуту)
    @Scheduled(fixedDelay = 60000)
    public void cleanCache() {
        int before = cache.size();
        cache.entrySet().removeIf(entry -> entry.getValue().isExpired());
        int after = cache.size();
        if (before != after) {
            System.out.println("🧹 Очистка кэша: удалено " + (before - after) + " записей, осталось " + after);
        }
    }

    // Внутренний класс для хранения данных с временем жизни
    private static class CacheEntry {
        private final byte[] data;
        private final long timestamp;

        public CacheEntry(byte[] data) {
            this.data = data;
            this.timestamp = Instant.now().toEpochMilli();
        }

        public byte[] getData() {
            return data;
        }

        public boolean isExpired() {
            return Instant.now().toEpochMilli() - timestamp > CACHE_TTL;
        }
    }
}