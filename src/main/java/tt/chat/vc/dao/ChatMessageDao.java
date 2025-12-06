package tt.chat.vc.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tt.chat.vc.entity.ChatMessage;

import java.util.List;
//Чат уведомления
@Repository
public interface ChatMessageDao extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatId(String chatId);
    List<ChatMessage> findByChatIdAndSenderId(String chatId, String senderId);

}
