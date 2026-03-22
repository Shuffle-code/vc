package tt.chat.vc.dao;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tt.chat.vc.entity.StreamChatMessage;

import java.time.LocalDateTime;
import java.util.List;
//Чат уведомления
@Repository
public interface StreamChatMessageDao extends JpaRepository<StreamChatMessage, Long> {

    List<StreamChatMessage> findAll();
//    List<StreamChatMessage> findByStreamId(Long streamId);
//    List<StreamChatMessage> findByStreamIdAndSenderId(Long streamId, Long senderId);
//    List<StreamChatMessage> findByStreamIdAndIdLessThanOrderByTimestampDesc(Long streamId, String string, PageRequest pageRequest); todo: написать метод поиска необходимого списка через запрос к БД

//
//    List<StreamChatMessage> findTop10ByOrderByTimestampDesc(Long streamId);
//    List<StreamChatMessage> findTop10ByStreamChatIdOrderByTimestampDesc(Long streamId);
//    @Query(value = "SELECT * FROM stream_chat_message " +
//            "WHERE stream_id = :streamId " +
//            "ORDER BY time_stamp DESC " +
//            "LIMIT 10", nativeQuery = true)
//    List<StreamChatMessage> findLast10MessagesByStreamId(@Param("streamId") Long streamId);


    @Query(value = "SELECT * FROM ( " +
            "    SELECT * FROM stream_chat_message " +
            "    WHERE stream_id = :streamId " +
            "    ORDER BY time_stamp DESC " +
            "    LIMIT 50 " +
            ") AS recent_messages " +
            "ORDER BY time_stamp ASC", nativeQuery = true)
    List<StreamChatMessage> findLast10MessagesByStreamId(@Param("streamId") Long streamId);

//
@Query("SELECT COUNT(scm) FROM StreamChatMessage scm " +
        "WHERE scm.observer.id = :senderId " +
        "AND scm.streamChat.id = :streamId " +
        "AND scm.timestamp >= :oneSecondAgo")
int countBySenderIdAndStreamIdAndTimestampAfter(@Param("senderId") Long senderId,
                                                @Param("streamId") Long streamId,
                                                @Param("oneSecondAgo") LocalDateTime oneSecondAgo);
}
