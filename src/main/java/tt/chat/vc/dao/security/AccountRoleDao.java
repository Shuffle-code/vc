package tt.chat.vc.dao.security;

import tt.chat.vc.entity.security.AccountRole ;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountRoleDao extends JpaRepository<AccountRole, Long> {
    AccountRole findByName(String name);

    @Query(value = "SELECT user_role.ROLE_ID FROM user_role WHERE user_role.USER_ID = :id LIMIT 1", nativeQuery = true)
    Integer findRoleIdByUserId(@Param("id") Long id);
}
