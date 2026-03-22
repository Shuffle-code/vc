package tt.chat.vc.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import tt.chat.vc.entity.security.AccountUser;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageDto {
    @JsonIgnore
    private Long id;
    private String content;
    private Long userId;
    private LocalDateTime timestamp;

    // Запрос на отправку сообщения
    @Data
    public class SendMessageRequest {
        private String content;
    }

    // Ответ с историей сообщений
    @Data
    public class ChatHistoryResponse {
        private List<MessageDto> messages;
        private boolean hasMore;
        private int total;
    }
}