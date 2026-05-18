package tt.chat.vc.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class WebSocketSessionService {
    private final ConcurrentHashMap<String, SessionInfo> sessionMap = new ConcurrentHashMap<>();

    @Data
    @AllArgsConstructor
    public static class SessionInfo {
        private Long userId;
        private Long streamId;
        private String closeStatus;
    }

    // Сохраняем информацию о сессии при подключении
    public void registerSession(String sessionId, Long userId, Long streamId) {
        sessionMap.put(sessionId, new SessionInfo(userId, streamId, null));
        log.info("Session registered: {} for user {} in stream {}", sessionId, userId, streamId);
    }

    // Обновляем статус закрытия
    public void updateCloseStatus(String sessionId, String closeStatus) {
        SessionInfo info = sessionMap.get(sessionId);
        if (info != null) {
            info.setCloseStatus(closeStatus);
        }
    }

    // Получаем информацию о сессии
    public SessionInfo getSessionInfo(String sessionId) {
        return sessionMap.get(sessionId);
    }

    // Удаляем сессию
    public void removeSession(String sessionId) {
        sessionMap.remove(sessionId);
        log.info("Session removed: {}", sessionId);
    }

    // Получаем streamId по sessionId
    public Long getStreamIdForSession(String sessionId) {
        SessionInfo info = sessionMap.get(sessionId);
        return info != null ? info.getStreamId() : null;
    }

    // Получаем userId по sessionId
    public Long getUserIdForSession(String sessionId) {
        SessionInfo info = sessionMap.get(sessionId);
        return info != null ? info.getUserId() : null;
    }
}