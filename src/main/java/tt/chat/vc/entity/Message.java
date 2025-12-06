package tt.chat.vc.entity;
import jakarta.persistence.*;
import lombok.*;
import tt.chat.vc.entity.common.BaseEntity;
import tt.chat.vc.entity.security.AccountUser;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
//@RequiredArgsConstructor
//@AllArgsConstructor
@Entity
@Table(name = "message")
public class Message extends BaseEntity {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;

    @Column(nullable = false, length = 1000)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_user_id")
    private AccountUser accountUser;

    private LocalDateTime timestamp;

    public Message(String content, AccountUser accountUser) {
    }

//    public Message(String content, AccountUser accountUser) {
//    }

    @PrePersist

    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }

//    // Constructors
//    public Message() {}
//
//    public Message(String content, AccountUser accountUser) {
//        this.content = content;
//        this.accountUser = accountUser;
//    }

//    // Getters and Setters
//    public Long getId() { return id; }
//    public void setId(Long id) { this.id = id; }
//
//    public String getContent() { return content; }
//    public void setContent(String content) { this.content = content; }
//
//    public AccountUser getUser() { return accountUser; }
//    public void setUser(AccountUser accountUser) { this.accountUser = accountUser; }
//
//    public LocalDateTime getTimestamp() { return timestamp; }
//    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}