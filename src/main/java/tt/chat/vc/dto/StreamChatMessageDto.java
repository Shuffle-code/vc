package tt.chat.vc.dto;

import lombok.*;
import tt.chat.vc.entity.enums.MessageType;
import tt.chat.vc.entity.enums.StreamChatMessageStatus;
import tt.chat.vc.entity.enums.StreamChatStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
//@RequiredArgsConstructor
@Builder
public class StreamChatMessageDto {
    private Long id;
    private Long streamId;
    private Long senderId;
    private String username;
    private String content;
    private LocalDateTime timestamp;
    private StreamChatMessageStatus status = StreamChatMessageStatus.RUN;

    public StreamChatMessageDto(Long id, String content, String username,StreamChatStatus streamChatStatus, LocalDateTime timestamp) {
    }

    public StreamChatMessageDto(Long id, String username, Long streamId, String content, StreamChatMessageStatus streamChatMessageStatus, LocalDateTime timestamp) {
    }

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
