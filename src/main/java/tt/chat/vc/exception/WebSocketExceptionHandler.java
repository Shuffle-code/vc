package tt.chat.vc.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.security.Principal;

@Slf4j
// Глобальный обработчик для WebSocket ошибок
@ControllerAdvice
public class WebSocketExceptionHandler {

    @MessageExceptionHandler
    public void handleChatException(ChatException e, Principal principal) {
        log.error("Chat error for user {}: {}", principal.getName(), e.getMessage());
        // Можно отправить ошибку обратно клиенту
    }
}