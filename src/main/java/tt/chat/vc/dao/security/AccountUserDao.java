package tt.chat.vc.dao.security;

import tt.chat.vc.entity.security.AccountUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountUserDao extends JpaRepository<AccountUser, Long> {
    Optional<AccountUser> findByUsername(String username);
}
