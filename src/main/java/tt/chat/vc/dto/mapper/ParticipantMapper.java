package tt.chat.vc.dto.mapper;

import tt.chat.vc.entity.security.AccountUser;
import org.mapstruct.Mapper;
import tt.chat.vc.entity.Observer;


@Mapper(componentModel = "spring")
public interface ParticipantMapper {
    AccountUser toAccountUser(Observer observer);
    Observer toObserver(AccountUser accountUser);
}
