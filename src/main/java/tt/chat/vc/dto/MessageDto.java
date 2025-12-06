package tt.chat.vc.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageDto {
    @JsonIgnore
    private Long id;
    private String content;
    private String username;
    private LocalDateTime timestamp;

    // Constructors, Getters and Setters
//    public MessageDto() {}

//    public MessageDto(Long id, String content, String username, LocalDateTime timestamp) {
//        this.id = id;
//        this.content = content;
//        this.username = username;
//        this.timestamp = timestamp;
//    }

//    public Long getId() { return id; }
//    public void setId(Long id) { this.id = id; }
//
//    public String getContent() { return content; }
//    public void setContent(String content) { this.content = content; }
//
//    public String getUsername() { return username; }
//    public void setUsername(String username) { this.username = username; }
//
//    public LocalDateTime getTimestamp() { return timestamp; }
//    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}