package tt.chat.vc.controller;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import tt.chat.vc.dto.StreamChatMessageDto;
import tt.chat.vc.dto.UserDto;
import tt.chat.vc.entity.JoinMessage;
import tt.chat.vc.entity.enums.StreamChatMessageStatus;
import tt.chat.vc.entity.security.AccountUser;
import tt.chat.vc.service.ChatService;
import tt.chat.vc.service.UserService;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatService chatService;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;


    /**
     * Получение сообщения от клиента
     * Клиент отправляет в /app/streams/{streamId}/message
     */
    @MessageMapping("/streams/{streamId}/message")
    public void handleChatMessage(
            @DestinationVariable Long streamId,
            @RequestBody String content,
            Principal principal,
            SimpMessageHeaderAccessor headerAccessor) {

        log.info("WebSocket message received: stream={}, user={}, content={}",
                streamId, principal.getName(), content);

        try {
            // Отправляем сообщение через сервис
            chatService.sendMessage(streamId, userService.findByUsername(principal.getName()).getId(), content);

        } catch (Exception e) {
            log.error("Error processing message: {}", e.getMessage());
            // Отправляем ошибку обратно пользователю
            headerAccessor.getSessionAttributes().put("error", e.getMessage());
        }
    }

    /**
     * Обработка подписки на топик
     * Вызывается когда клиент подписывается на /topic/streams/{streamId}
     */
//    @SubscribeMapping("/streams/{streamId}")
//    public void handleSubscription(
//            @DestinationVariable Long streamId,
//            Principal principal,
//            SimpMessageHeaderAccessor headerAccessor) {
//        log.info("User {} subscribed to stream {}", principal.getName(), streamId);
//        // Сохраняем в сессии информацию о подписке
//        headerAccessor.getSessionAttributes().put("streamId", streamId);
//        headerAccessor.getSessionAttributes().put("userId", principal.getName());
//        // Отправляем уведомление о подключении
//        chatService.userJoined(streamId, userService.findByUsername(principal.getName()).getId());
//        return new StreamChatMessage("Welcome to stream " + streamId + "!");
//        return chatService.getRecentStreamChatMessages(streamId);
//    }

//    private final Map<String, SessionInfo> activeSessions = new ConcurrentHashMap<>();
//
//    @EventListener
//    public void handleSessionConnected(SessionConnectedEvent event) {
//        StompHeaderAccessor headers = StompHeaderAccessor.wrap(event.getMessage());
//        String sessionId = headers.getSessionId();
//
//        log.info("🔌 New session connected: {}", sessionId);
//    }
//
//    @EventListener
//    public void handleSessionSubscribeEvent(SessionSubscribeEvent event) {
//        StompHeaderAccessor headers = StompHeaderAccessor.wrap(event.getMessage());
//        String destination = headers.getDestination();
//
//        if (destination != null && destination.startsWith("/topic/streams/")) {
//            String sessionId = headers.getSessionId();
//            Long streamId = Long.parseLong(destination.replace("/topic/streams/", ""));
//            Principal principal = headers.getUser();
//
//            if (principal != null) {
//                Long userId = getUserIdFromPrincipal(principal);
//
//                // Проверяем, была ли уже сессия для этого пользователя
//                SessionInfo existingSession = findSessionByUserIdAndStream(userId, streamId);
//
//                if (existingSession != null) {
//                    log.info("🔄 User {} reconnected to stream {}, replacing old session", userId, streamId);
//                    // Удаляем старую сессию без отправки LEAVE
//                    activeSessions.remove(existingSession.getSessionId());
//                }
//
//                // Сохраняем новую сессию
//                SessionInfo sessionInfo = SessionInfo.builder()
//                        .sessionId(sessionId)
//                        .userId(userId)
//                        .streamId(streamId)
//                        .connectedAt(LocalDateTime.now())
//                        .build();
//
//                activeSessions.put(sessionId, sessionInfo);
//
//                // Сохраняем в атрибуты сессии
//                headers.getSessionAttributes().put("streamId", streamId);
//                headers.getSessionAttributes().put("userId", userId);
//
//                log.info("✅ User {} joined stream {} (session: {})", userId, streamId, sessionId);
//
//                // Отправляем JOIN только если это не переподключение
//                if (existingSession == null) {
//                    sendJoinNotification(streamId, userId);
//                }
//            }
//        }
//    }
//
//    @EventListener
//    public void handleSessionDisconnect(SessionDisconnectEvent event) {
//        StompHeaderAccessor headers = StompHeaderAccessor.wrap(event.getMessage());
//        String sessionId = headers.getSessionId();
//
//        SessionInfo sessionInfo = activeSessions.remove(sessionId);
//
//        if (sessionInfo != null) {
//            Long streamId = sessionInfo.getStreamId();
//            Long userId = sessionInfo.getUserId();
//
//            // Проверяем, есть ли другие активные сессии для этого пользователя
//            boolean hasOtherSessions = activeSessions.values().stream()
//                    .anyMatch(s -> s.getUserId().equals(userId) && s.getStreamId().equals(streamId));
//
//            if (!hasOtherSessions) {
//                // Нет других сессий - пользователь действительно покинул чат
//                log.info("👋 User {} left stream {} (no other sessions)", userId, streamId);
//                sendLeaveNotification(streamId, userId);
//                chatService.userLeft(streamId, userId);
//            } else {
//                log.info("🔄 User {} has other active sessions, skipping LEAVE", userId);
//            }
//        }
//    }
//
//    private void sendJoinNotification(Long streamId, Long userId) {
//        UserDto user = userService.findById(userId);
//        if (user != null) {
//            StreamChatMessageDto joinNotification = StreamChatMessageDto.builder()
//                    .content(user.getUsername() + " присоединился к чату")
//                    .status(StreamChatMessageStatus.JOIN)
//                    .senderId(userId)
//                    .username(user.getUsername())
//                    .timestamp(LocalDateTime.now())
//                    .build();
//
//            messagingTemplate.convertAndSend("/topic/streams/" + streamId, joinNotification);
//        }
//    }
//
//    private void sendLeaveNotification(Long streamId, Long userId) {
//        UserDto user = userService.findById(userId);
//        if (user != null) {
//            StreamChatMessageDto leaveNotification = StreamChatMessageDto.builder()
//                    .content(user.getUsername() + " покинул чат")
//                    .status(StreamChatMessageStatus.LEAVE)
//                    .senderId(userId)
//                    .username(user.getUsername())
//                    .timestamp(LocalDateTime.now())
//                    .build();
//
//            messagingTemplate.convertAndSend("/topic/streams/" + streamId, leaveNotification);
//        }
//    }
//
//    private Long getUserIdFromPrincipal(Principal principal) {
//        // Логика получения userId из principal
//        return userService.findByUsername(principal.getName()).getId();
//    }
//
//    private SessionInfo findSessionByUserIdAndStream(Long userId, Long streamId) {
//        return activeSessions.values().stream()
//                .filter(s -> s.getUserId().equals(userId) && s.getStreamId().equals(streamId))
//                .findFirst()
//                .orElse(null);
//    }
//
//    @Data
//    @Builder
//    private static class SessionInfo {
//        private String sessionId;
//        private Long userId;
//        private Long streamId;
//        private LocalDateTime connectedAt;
//    }

    /**
     * Обработка отключения
     * Регистрируется через @EventListener
     */
    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        Long streamId = (Long) headerAccessor.getSessionAttributes().get("streamId");
        Long userId = (Long) headerAccessor.getSessionAttributes().get("userId");
        // Получаем причину отключения
        String closeStatus = (String) headerAccessor.getSessionAttributes().get("closeStatus");
        log.info(closeStatus);
        if (streamId != null && userId != null) {
            // Отправляем LEAVE только при явном выходе
            if ("logout".equals(closeStatus) || "session-expired".equals(closeStatus) || "forced-logout".equals(closeStatus)) {
                log.info("User {} logged out from stream {}", userId, streamId);
                chatService.userLeft(streamId, userId);
            } else {
                log.info("User {} disconnected (page reload/tab close), skipping LEAVE", userId);
                // Не отправляем LEAVE при перезагрузке
            }
        }
    }


    @MessageMapping("/streams/{streamId}/join")
    public void handleJoin(
            @DestinationVariable Long streamId,
            @Payload JoinMessage joinMessage,
            Principal principal,
            SimpMessageHeaderAccessor headerAccessor) {
        String username = Optional.ofNullable(principal)
                .map(Principal::getName)
                .map(userService::findByUsername)
                .map(AccountUser::getObserver)
                .map(observer -> observer.getFirstname() + " " + observer.getLastname())
                .filter(name -> !name.trim().isEmpty())
                .orElse("Гость");
        // Сохраняем в сессии
        headerAccessor.getSessionAttributes().put("streamId", streamId);
        headerAccessor.getSessionAttributes().put("userId", joinMessage.getUserId());
        headerAccessor.getSessionAttributes().put("username", username);
//        headerAccessor.getSessionAttributes().put("action", joinMessage.getAction());

        StreamChatMessageDto joinNotification = StreamChatMessageDto.builder()
                .content(" присоединился к чату")
                .status(StreamChatMessageStatus.JOIN)
                .senderId(joinMessage.getUserId())
                .username(username)
                .timestamp(LocalDateTime.now())
                .build();
       if ("LOGIN".equals(joinMessage.getAction())){
           messagingTemplate.convertAndSend("/topic/streams/" + streamId, joinNotification);
           headerAccessor.getSessionAttributes().put("action", "RECONNECT");
       }else log.info(joinMessage.getAction() + " " + "LOGIN".equals(joinMessage.getAction()));
    }
}