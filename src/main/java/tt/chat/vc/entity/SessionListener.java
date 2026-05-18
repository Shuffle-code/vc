package tt.chat.vc.entity;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import tt.chat.vc.entity.enums.Status;
import tt.chat.vc.entity.security.AccountUser;
import tt.chat.vc.entity.security.enums.AccountStatus;
import tt.chat.vc.service.UserService;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
@RequiredArgsConstructor
public class SessionListener implements HttpSessionListener {
    private final UserService userService;

    private static final Map<String, String> onlineUsers = new ConcurrentHashMap<>();
    private static AtomicInteger activeSessions = new AtomicInteger(0);
    private static AtomicInteger authenticatedSessions = new AtomicInteger(0);
    @Override
    public void sessionCreated(HttpSessionEvent event) {
            activeSessions.incrementAndGet();
            log.info("Сессия создана. Активных сессий: " + activeSessions.get());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        activeSessions.updateAndGet(value -> value > 0 ? value - 1 : 0);
        HttpSession session = event.getSession();
        // Проверяем, была ли сессия аутентифицирована
        AccountUser accountUser = getUserFromSession(session);
        if (session.getAttribute("SPRING_SECURITY_CONTEXT") != null) {
            authenticatedSessions.decrementAndGet();
        }
        log.info("Сессия уничтожена. Активных сессий: " + activeSessions.get());
        if (accountUser != null) {
            // Сессия истекла без выхода - ставим OFFLINE
            userService.updateUserStatus(accountUser, AccountStatus.OFFLINE, Status.OFFLINE);
            log.info("Сессия пользователя {} истекла. Статус: OFFLINE", accountUser.getUsername());
        }
    }

    private AccountUser getUserFromSession(HttpSession session) {
        // Пробуем из атрибута
        AccountUser accountUser = (AccountUser) session.getAttribute("user");
        if (accountUser != null) return accountUser;
//        log.info(accountUser.getFirstname());

        // Пробуем из SecurityContext
        SecurityContext context = (SecurityContext) session
                .getAttribute("SPRING_SECURITY_CONTEXT");
        if (context != null && context.getAuthentication() != null) {
            Object principal = context.getAuthentication().getPrincipal();
            if (principal instanceof AccountUser) {
                return (AccountUser) principal;
            }
        }
        return null;
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
    public static int getAuthenticatedUserCount() {
        return authenticatedSessions.get();
    }
    public static List<String> getOnlineUsers() {
        return new ArrayList<>(onlineUsers.values());
    }

    public static AtomicInteger getActiveSessions(){
        return activeSessions;
    }

    public static void userAuthenticated(HttpSession session) {
        authenticatedSessions.incrementAndGet();
    }
}


