package tt.chat.vc.dao.security;

import tt.chat.vc.entity.security.ConfirmationCode ;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ConfirmationCodeDao extends JpaRepository<ConfirmationCode, Long> {
    ConfirmationCode findConfirmationCodeByAccountUser_Id (Long id);
}
