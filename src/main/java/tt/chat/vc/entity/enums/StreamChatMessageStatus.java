package tt.chat.vc.entity.enums;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
@Getter
@RequiredArgsConstructor
public enum StreamChatMessageStatus {
    RUN("RUN"), SYSTEM("SYSTEM"), LEAVE("LEAVE"), STOPPED("STOPPED"), JOIN("JOIN"), CHAT("CHAT"), USER("USER");
    private final String title;
}


