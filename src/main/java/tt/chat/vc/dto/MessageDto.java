package tt.chat.vc.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import tt.chat.vc.entity.security.AccountUser;

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
//    private AccountUser accountUser;
    private Long userId;
    private LocalDateTime timestamp;
}