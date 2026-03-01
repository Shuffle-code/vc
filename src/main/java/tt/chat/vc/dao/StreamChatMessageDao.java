package tt.chat.vc.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tt.chat.vc.entity.StreamChatMessage;

import java.util.List;
//Чат уведомления
@Repository
public interface StreamChatMessageDao extends JpaRepository<StreamChatMessage, Long> {
    List<StreamChatMessage> findByChatId(String chatId);
    List<StreamChatMessage> findByChatIdAndSenderId(String chatId, String senderId);

}
