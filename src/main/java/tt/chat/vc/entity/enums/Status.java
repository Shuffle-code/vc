package tt.chat.vc.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Status {
    ACTIVE("Оплачено"), DISABLE("Дисквалификация"), NOT_ACTIVE("Не оплачено"), DELETED("Черный список");
    private final String title;
}
