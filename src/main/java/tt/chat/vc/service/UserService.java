package tt.chat.vc.service;

import tt.chat.vc.dto.UserDto;
import tt.chat.vc.entity.enums.Status;
import tt.chat.vc.entity.security.AccountUser;
import tt.chat.vc.entity.security.enums.AccountStatus;

import java.io.IOException;
import java.util.List;
//@Service
//@Component
public interface UserService {

    UserDto register(UserDto userDto) throws IOException;
    UserDto update(UserDto userDto);
    AccountUser findByUsername(String username);
    AccountUser update(AccountUser userDto);
    UserDto findById(Long id);
    List<UserDto> findAll();
    String getConfirmationCode();
    void deleteById(Long id);
    void generateConfirmationCode(UserDto thisUser, String confirmationCode);
    void disconnect(AccountUser accountUser);
    List<AccountUser> findAllByStatus(AccountStatus accountStatus);

    void updateUserStatus(AccountUser accountUser, AccountStatus accountStatus, Status status);

}
