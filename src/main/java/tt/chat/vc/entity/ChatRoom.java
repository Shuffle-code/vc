package tt.chat.vc.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import tt.chat.vc.entity.common.BaseEntity;

@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
//@NoArgsConstructor
@Builder
@Table(name="chat_room")
public class ChatRoom extends BaseEntity {
    private String chatId;
    private String senderId; // отправитель
    private String recipientId; // получатель
}
