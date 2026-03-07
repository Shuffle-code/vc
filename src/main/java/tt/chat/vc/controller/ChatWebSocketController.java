package tt.chat.vc.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import tt.chat.vc.dto.MessageDto;
import tt.chat.vc.service.ChatService;

import java.security.Principal;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatService chatService;

    /**
     * Получение сообщения от клиента
     * Клиент отправляет в /app/streams/{streamId}/message
     */
    @MessageMapping("/streams/{streamId}/message")
    public void handleChatMessage(
            @DestinationVariable Long streamId,
            @Payload MessageDto.SendMessageRequest request,
            Principal principal,
            Authentication authentication,
            SimpMessageHeaderAccessor headerAccessor) {

        log.info("WebSocket message received: stream={}, user={}, content={}",
                streamId, principal.getName(), request.getContent());

        try {
            // Отправляем сообщение через сервис
            chatService.sendMessage(streamId, principal.getName(), request.getContent());

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
    @SubscribeMapping("/topic/streams/{streamId}")
    public void handleSubscription(
            @DestinationVariable Long streamId,
            Principal principal,
            SimpMessageHeaderAccessor headerAccessor) {

        log.info("User {} subscribed to stream {}", principal.getName(), streamId);

        // Сохраняем в сессии информацию о подписке
        headerAccessor.getSessionAttributes().put("streamId", streamId);
        headerAccessor.getSessionAttributes().put("userId", principal.getName());

        // Отправляем уведомление о подключении
        chatService.userJoined(streamId, principal.getName());
    }

    /**
     * Обработка отключения
     * Регистрируется через @EventListener
     */
    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());

        Long streamId = (Long) headerAccessor.getSessionAttributes().get("streamId");
        String userId = (String) headerAccessor.getSessionAttributes().get("userId");

        if (streamId != null && userId != null) {
            log.info("User {} disconnected from stream {}", userId, streamId);
            chatService.userLeft(streamId, userId);
        }
    }
}