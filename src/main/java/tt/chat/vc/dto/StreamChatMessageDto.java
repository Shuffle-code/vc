package tt.chat.vc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tt.chat.vc.entity.enums.MessageType;
import tt.chat.vc.entity.enums.StreamChatStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StreamChatMessageDto {
    private String id;
    private Long streamId;
    private String senderId;
    private String username;
    private String content;
    private LocalDateTime timestamp;
    private StreamChatStatus type = StreamChatStatus.RUN;

//    public enum MessageType {
//        CHAT, JOIN, LEAVE, SYSTEM
//    }
    @Data
    public class SendMessageRequest {
        private String content;
    }

    // Ответ с историей сообщений
    @Data
    public class ChatHistoryResponse {
        private List<StreamChatMessageDto> messages;
        private boolean hasMore;
        private int total;
    }
}
