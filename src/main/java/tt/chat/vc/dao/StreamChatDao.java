package tt.chat.vc.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tt.chat.vc.entity.Message;
import tt.chat.vc.entity.StreamChat;

@Repository
public interface StreamChatDao extends JpaRepository<StreamChat, Long> {
    boolean existsByTournamentId(Long tournamentId);
}
