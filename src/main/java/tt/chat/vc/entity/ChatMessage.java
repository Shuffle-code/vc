package tt.chat.vc.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import tt.chat.vc.entity.common.BaseEntity;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name ="chat_message")
public class ChatMessage extends BaseEntity {
    private String chatId;
    private String senderId;
    private String recipientId;
    private String content;
    private Date timeStamp;
}
