package tt.chat.vc.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TourStatus {
    UPCOMING("Предстаящий турнир"), ACTIVE("Текущий турнир"), FINISHED("Завершенный турнир"), DELETED("Удалён");
    private final String title;
}
