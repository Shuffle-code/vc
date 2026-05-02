package tt.chat.vc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tt.chat.vc.dao.StreamChatMessageDao;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableScheduling
public class MessageCleanupService {
    private final StreamChatMessageDao streamChatMessageDao;
    // Запускаем каждый день в 3:00 ночи
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void cleanupExpiredMessages() {
        LocalDateTime now = LocalDateTime.now();
        int deletedCount = streamChatMessageDao.deleteExpiredMessages(now);
        if (deletedCount > 0) {
            log.info("🗑️ Удалено {} старых сообщений (старше 30 дней)", deletedCount);
        }
    }
}
