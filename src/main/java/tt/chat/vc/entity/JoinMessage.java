package tt.chat.vc.entity;

import lombok.Data;

import java.util.Objects;

@Data
public class JoinMessage {
    private Long userId;
    private String username;
    private String action;
    private String timestamp;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JoinMessage joinMessage = (JoinMessage) o;
        return userId.equals(joinMessage.getUserId()) && username.equals(joinMessage.username) &&
                action.equals(joinMessage.action) && timestamp.equals(joinMessage.timestamp);
    }
    @Override
    public int hashCode() {
        return Objects.hash(userId, username, action, timestamp);
    }
}