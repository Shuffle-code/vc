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
import tt.chat.vc.entity.SessionListener;
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

//        log.info("WebSocket message received: stream={}, user={}, content={}",
//                streamId, principal.getName(), content);

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
     * Обработка отключения
     * Регистрируется через @EventListener
     */
    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        Long streamId = (Long) headerAccessor.getSessionAttributes().get("streamId");
        Long userId = (Long) headerAccessor.getSessionAttributes().get("userId");
        String closeStatus = (String) headerAccessor.getSessionAttributes().get("closeStatus");
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
            StreamChatMessageDto joinNotification = StreamChatMessageDto.builder()
                    .content(" присоединился к чату")
                    .status(StreamChatMessageStatus.JOIN)
                    .senderId(joinMessage.getUserId())
                    .username(username)
                    .timestamp(LocalDateTime.now())
                    .build();
            messagingTemplate.convertAndSend("/topic/streams/" + streamId, joinNotification);
    }
}