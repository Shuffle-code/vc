package tt.chat.vc.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Status {
    ACTIVE("ACTIVE"), DISABLE("Дисквалификация"),
    NOT_ACTIVE("NOT_ACTIVE"), DELETED("Черный список"),
    OFFLINE("Автономный"), ONLINE ("В сети");
    private final String title;
}
