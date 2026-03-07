package tt.chat.vc.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import tt.chat.vc.entity.common.BaseEntity;
import tt.chat.vc.entity.security.AccountUser;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name ="stream_chat_message")
public class StreamChatMessage extends BaseEntity {
    @Column(name = "stream_id")
    private String streamId;
    @Column(name = "sender_id")
    private String senderId;
    @Column(nullable = false, length = 1000)
    private String content;
    @Column(name = "time_stamp")
    private LocalDateTime timeStamp;

//    @ManyToOne
//    @JoinColumn(name = "stream_chat_id")
////    @JsonIgnore
//    private StreamChat streamChat;

    @ManyToOne
    @JoinColumn(name = "account_user_id")
    @JsonIgnore
    private AccountUser accountUser;

}
