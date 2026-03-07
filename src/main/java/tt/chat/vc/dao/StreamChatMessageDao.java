package tt.chat.vc.dao;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tt.chat.vc.entity.StreamChatMessage;

import java.time.LocalDateTime;
import java.util.List;
//Чат уведомления
@Repository
public interface StreamChatMessageDao extends JpaRepository<StreamChatMessage, Long> {
    List<StreamChatMessage> findByStreamId(String streamId);
    List<StreamChatMessage> findByStreamIdAndSenderId(String streamId, String senderId);
    List<StreamChatMessage> findByStreamIdAndIdLessThanOrderByTimeStampDesc(Long streamId, String string, PageRequest pageRequest);

    List<StreamChatMessage> findByStreamIdOrderByTimeStampDesc(Long streamId, PageRequest pageRequest);

    int countBySenderIdAndTimeStampAfter(String userId, LocalDateTime oneSecondAgo);
}
