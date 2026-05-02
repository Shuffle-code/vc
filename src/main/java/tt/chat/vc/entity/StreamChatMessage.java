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

    // Добавляем поле для автоматического удаления
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    // Автоматически устанавливаем expires_at при создании
    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
        // Сообщение будет жить 30 дней
        if (expiresAt == null) {
            expiresAt = timestamp.plusDays(30);
        }
    }

    @ManyToOne
    @JoinColumn(name = "sender_id")
    @JsonIgnore
    private Observer observer;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StreamChatMessageStatus streamChatMessageStatus;
}
