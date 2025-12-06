package tt.chat.vc.dao.security;

import tt.chat.vc.entity.security.AccountUser;
import org.springframework.data.jpa.repository.JpaRepository;
import tt.chat.vc.entity.security.enums.AccountStatus;

import java.util.List;
import java.util.Optional;

public interface AccountUserDao extends JpaRepository<AccountUser, Long> {
    Optional<AccountUser> findByUsername(String username);
    List<AccountUser> findAllByStatus(AccountStatus accountStatus);

    AccountUser findByStatus(AccountUser accountUser);

}
