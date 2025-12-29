package tt.chat.vc.dao.security;

import org.springframework.data.jpa.repository.Query;
import tt.chat.vc.entity.security.AccountUser;
import org.springframework.data.jpa.repository.JpaRepository;
import tt.chat.vc.entity.security.enums.AccountStatus;

import java.util.List;
import java.util.Optional;

public interface AccountUserDao extends JpaRepository<AccountUser, Long> {
    Optional<AccountUser> findByUsername(String username);
    List<AccountUser> findAllByStatus(AccountStatus accountStatus);
//    List<AccountUser> findAllByEnabled();

    AccountUser findByStatus(AccountUser accountUser);
    @Query(value = "SELECT OBSERVER_ID FROM account_user where ENABLED = true", nativeQuery = true)
    List<Long> getAllByEnabled();
}
