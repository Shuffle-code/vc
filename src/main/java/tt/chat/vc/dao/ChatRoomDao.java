package tt.chat.vc.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tt.chat.vc.entity.ChatRoom;

import java.util.Optional;

@Repository
public interface ChatRoomDao extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findBySenderIdAndRecipientId(String senderId, String recipientId);
}
