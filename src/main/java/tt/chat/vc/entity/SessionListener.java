package tt.chat.vc.entity;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class SessionListener implements HttpSessionListener {

    private static final Map<String, String> onlineUsers = new ConcurrentHashMap<>();
    private static AtomicInteger activeSessions = new AtomicInteger(0);
    @Override
    public void sessionCreated(HttpSessionEvent event) {
            activeSessions.incrementAndGet();
            log.info("Сессия создана. Активных сессий: " + activeSessions.get());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        activeSessions.decrementAndGet();
    }

    public static String addUser(String sessionId, String username) {
        onlineUsers.put(sessionId, username);
        return sessionId;
    }

    public static String findFirstSessionIdByUsername(String username) {
        if (username == null) {
            return null; // или выбросить исключение
        }
        return onlineUsers.entrySet().stream()
                .filter(entry -> entry.getValue().equals(username))
                .map(Map.Entry::getKey)
                .findFirst()  // гарантированно вернет первую в порядке итерации
                .orElse(null);
    }

    public static void deleteUser(String sessionId, String username) {
        if (sessionId == null || username == null) {
            log.info("sessionId или username равны null");
            return;
        }

        onlineUsers.entrySet().removeIf(entry ->
                username.equals(entry.getValue()) && sessionId.equals(entry.getKey())
        );
    }

    public static int countOnlineUsers(){
        return onlineUsers.size();
    }

    public static List<String> getOnlineUsers() {
        return new ArrayList<>(onlineUsers.values());
    }

    public static AtomicInteger getActiveSessions(){
        return activeSessions;
    }
}