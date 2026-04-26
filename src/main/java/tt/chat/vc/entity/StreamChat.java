package tt.chat.vc.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tt.chat.vc.entity.common.BaseEntity;
import tt.chat.vc.entity.enums.Status;
import tt.chat.vc.entity.enums.StreamChatStatus;
@Entity
@NoArgsConstructor
@Setter
@Getter
@Table(name = "stream_chat")
public class StreamChat extends BaseEntity {
    @Column(name = "title")
    private String title;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StreamChatStatus status;
    private Long ownerId; // владелец
    private Long tournamentId;

    @Builder
    public StreamChat(Long id, String title, Long ownerId, Long tournamentId) {
        super(id);
        this.title = title;
        this.status = StreamChatStatus.CHAT;
        this.ownerId = ownerId;
        this.tournamentId = tournamentId;
    }
}
