package tt.chat.vc.dao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tt.chat.vc.entity.Message;

import java.util.List;

@Repository
public interface MessageDao extends JpaRepository<Message, Long> {
    @Query("SELECT m FROM Message m ORDER BY m.timestamp ASC")
    List<Message> findAllOrderByTimestamp();

    List<Message> findTop50ByOrderByTimestampDesc();
}