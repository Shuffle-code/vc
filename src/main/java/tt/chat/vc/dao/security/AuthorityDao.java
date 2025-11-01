package tt.chat.vc.dao.security;

import tt.chat.vc.entity.security.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AuthorityDao extends JpaRepository<Authority, Long> {
}
