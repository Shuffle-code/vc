package tt.chat.vc.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SessionInfo {
    private String sessionId;
    private Long userId;
    private Long streamId;
    private LocalDateTime connectedAt;
}
