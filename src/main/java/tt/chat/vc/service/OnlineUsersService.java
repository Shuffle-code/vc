package tt.chat.vc.service;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tt.chat.vc.entity.OnlineUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class OnlineUsersService {
    private final Map<String, OnlineUser> onlineUsers = new ConcurrentHashMap<>();
    private final ScheduledExecutorService cleanupScheduler =
            Executors.newScheduledThreadPool(1);

    // Таймаут неактивности в минутах
    private static final int INACTIVITY_TIMEOUT_MINUTES = 15;

    public OnlineUsersService() {
        // Запускаем очистку неактивных пользователей каждую минуту
        cleanupScheduler.scheduleAtFixedRate(
                this::cleanupInactiveUsers,
                1, 1, TimeUnit.MINUTES
        );
    }

    /**
     * Добавить/обновить пользователя
     */
    public void addOrUpdateUser(String sessionId, HttpServletRequest request) {
        HttpSession session = request.getSession();
        OnlineUser user = onlineUsers.get(sessionId);

        if (user == null) {
            user = new OnlineUser();
            user.setSessionId(sessionId);
            user.setIpAddress(getClientIp(request));
            user.setUserAgent(request.getHeader("User-Agent"));
            user.setFirstSeen(LocalDateTime.now());
            user.setLastActivity(LocalDateTime.now());
            user.setCurrentPage(getCurrentPage(request));
            user.setAuthenticated(false);

            // Проверяем, авторизован ли пользователь
            Object username = session.getAttribute("username");
            if (username != null) {
                user.setUsername(username.toString());
                user.setAuthenticated(true);
            }

            onlineUsers.put(sessionId, user);
            logNewUser(user);
        } else {
            // Обновляем активность существующего пользователя
            user.setLastActivity(LocalDateTime.now());
            user.setCurrentPage(getCurrentPage(request));

            // Обновляем статус аутентификации
            Object username = session.getAttribute("username");
            if (username != null && !user.isAuthenticated()) {
                user.setUsername(username.toString());
                user.setAuthenticated(true);
            }
        }
    }

    /**
     * Удалить пользователя по сессии
     */
    public void removeUser(String sessionId) {
        OnlineUser removed = onlineUsers.remove(sessionId);
        if (removed != null) {
            logUserLeft(removed);
        }
    }

    /**
     * Получить количество онлайн пользователей
     */
    public int getOnlineUsersCount() {
        return onlineUsers.size();
    }

    /**
     * Получить количество аутентифицированных пользователей
     */
    public int getAuthenticatedUsersCount() {
        return (int) onlineUsers.values().stream()
                .filter(OnlineUser::isAuthenticated)
                .count();
    }

    /**
     * Получить список всех онлайн пользователей
     */
    public List<OnlineUser> getAllOnlineUsers() {
        return new ArrayList<>(onlineUsers.values());
    }

    /**
     * Получить статистику по страницам
     */
    public Map<String, Long> getPageVisits() {
        return onlineUsers.values().stream()
                .filter(u -> u.getCurrentPage() != null)
                .collect(Collectors.groupingBy(
                        OnlineUser::getCurrentPage,
                        Collectors.counting()
                ));
    }

    /**
     * Получить статистику по браузерам
     */
    public Map<String, Long> getBrowserStats() {
        return onlineUsers.values().stream()
                .filter(u -> u.getUserAgent() != null)
                .map(this::parseBrowserFromUserAgent)
                .collect(Collectors.groupingBy(
                        browser -> browser,
                        Collectors.counting()
                ));
    }

    /**
     * Получить пользователей по IP
     */
    public Map<String, List<OnlineUser>> getUsersByIp() {
        return onlineUsers.values().stream()
                .collect(Collectors.groupingBy(OnlineUser::getIpAddress));
    }

    /**
     * Очистка неактивных пользователей
     */
    private void cleanupInactiveUsers() {
        LocalDateTime cutoff = LocalDateTime.now()
                .minusMinutes(INACTIVITY_TIMEOUT_MINUTES);

        List<String> toRemove = onlineUsers.entrySet().stream()
                .filter(entry -> entry.getValue().getLastActivity().isBefore(cutoff))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        toRemove.forEach(this::removeUser);

        if (!toRemove.isEmpty()) {
            System.out.println("Cleaned up " + toRemove.size() + " inactive users");
        }
    }

    /**
     * Получить текущую страницу из запроса
     */
    private String getCurrentPage(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();

        if (queryString != null && !queryString.isEmpty()) {
            return uri + "?" + queryString;
        }
        return uri;
    }

    /**
     * Парсинг браузера из User-Agent
     */
    private String parseBrowserFromUserAgent(OnlineUser user) {
        String userAgent = user.getUserAgent().toLowerCase();

        if (userAgent.contains("chrome") && !userAgent.contains("edg")) {
            return "Chrome";
        } else if (userAgent.contains("firefox")) {
            return "Firefox";
        } else if (userAgent.contains("safari") && !userAgent.contains("chrome")) {
            return "Safari";
        } else if (userAgent.contains("edge")) {
            return "Edge";
        } else if (userAgent.contains("opera")) {
            return "Opera";
        } else {
            return "Other";
        }
    }

    /**
     * Получить IP клиента
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // Для localhost
        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "127.0.0.1";
        }

        return ip;
    }

    /**
     * Логирование нового пользователя
     */
    private void logNewUser(OnlineUser user) {
        System.out.println("New user connected: " + user.getIpAddress() +
                " | Session: " + user.getSessionId().substring(0, 8) + "...");
    }

    /**
     * Логирование ухода пользователя
     */
    private void logUserLeft(OnlineUser user) {
        System.out.println("User left: " + user.getIpAddress() +
                " | Session: " + user.getSessionId().substring(0, 8) + "...");
    }

    /**
     * Получить сводную статистику
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalOnline", getOnlineUsersCount());
        stats.put("authenticated", getAuthenticatedUsersCount());
        stats.put("anonymous", getOnlineUsersCount() - getAuthenticatedUsersCount());
        stats.put("pageVisits", getPageVisits());
        stats.put("browserStats", getBrowserStats());
        stats.put("usersByIp", getUsersByIp().size());
        stats.put("timestamp", LocalDateTime.now());
        return stats;
    }
}
