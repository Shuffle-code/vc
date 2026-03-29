package tt.chat.vc.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import tt.chat.vc.entity.common.BaseEntity;
import tt.chat.vc.entity.enums.StreamChatMessageStatus;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
//@RequiredArgsConstructor
@Entity
@Builder
@Table(name ="stream_chat_message")
public class StreamChatMessage extends BaseEntity {
    @Column(nullable = false, length = 1000)
    private String content;
    @Column(name = "time_stamp")
    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "stream_id")
    private StreamChat streamChat;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    @JsonIgnore
    private Observer observer;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StreamChatMessageStatus streamChatMessageStatus;
}
