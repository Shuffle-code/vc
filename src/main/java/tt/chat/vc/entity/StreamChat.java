package tt.chat.vc.entity;

import jakarta.persistence.*;
import tt.chat.vc.entity.common.BaseEntity;
import tt.chat.vc.entity.enums.Status;
import tt.chat.vc.entity.enums.StreamChatStatus;
@Entity
@Table(name = "stream_chat")
public class StreamChat extends BaseEntity {
    @Column(name = "title")
    private String title;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StreamChatStatus status;
    private Long ownerId; // владелец
}
