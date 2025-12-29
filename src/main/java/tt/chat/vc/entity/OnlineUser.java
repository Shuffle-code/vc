package tt.chat.vc.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OnlineUser {
    private String sessionId;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime firstSeen;
    private LocalDateTime lastActivity;
    private String currentPage;
    private String username; // если пользователь авторизован
    private boolean isAuthenticated;
}
