package tt.chat.vc.service;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;

@Service
public class RtspTokenService {
    private static final String SECRET_KEY = "ea63be058bbb19f9e93228177cab9c749ebabc28151184bd547ebbf7208a8aaa";
    private static final int TOKEN_VALIDITY_SECONDS = 600;

    public String generateToken() throws NoSuchAlgorithmException {
        long time = Instant.now().getEpochSecond() + TOKEN_VALIDITY_SECONDS;
        String data = SECRET_KEY + time;

        // SHA-256
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));

        // Base64 URL safe
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(hash);

        return token;
    }

    public String getStreamUrl() throws NoSuchAlgorithmException {
        long time = Instant.now().getEpochSecond() + TOKEN_VALIDITY_SECONDS;
        String token = generateToken();
        return String.format("https://rtsp.ru/private/333648/%s/%d/", token, time);
    }
}
