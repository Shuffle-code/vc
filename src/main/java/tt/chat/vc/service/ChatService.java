package tt.chat.vc.service;

        import lombok.RequiredArgsConstructor;
        import lombok.extern.slf4j.Slf4j;
        import org.springframework.data.domain.PageRequest;
        import org.springframework.data.domain.Sort;
        import org.springframework.messaging.simp.SimpMessagingTemplate;
        import org.springframework.stereotype.Service;
        import org.springframework.transaction.annotation.Transactional;
        import tt.chat.vc.dao.MessageDao;
        import tt.chat.vc.dao.StreamChatDao;
        import tt.chat.vc.dao.StreamChatMessageDao;
        import tt.chat.vc.dao.security.AccountUserDao;
        import tt.chat.vc.dto.StreamChatMessageDto;
        import tt.chat.vc.entity.Message;
        import tt.chat.vc.entity.StreamChat;
        import tt.chat.vc.entity.StreamChatMessage;
        import tt.chat.vc.entity.enums.StreamChatStatus;
        import tt.chat.vc.entity.security.AccountUser;
        import tt.chat.vc.entity.security.enums.AccountStatus;
        import tt.chat.vc.exception.ChatException;

        import java.time.LocalDateTime;
        import java.util.List;
        import java.util.UUID;
        import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageDao messageDao;
    private final StreamChatMessageDao streamChatMessageDao;
    private final AccountUserDao accountUserDao;
    private final StreamChatDao streamChatDao;

    // Константы ограничений
    private static final int MAX_MESSAGE_LENGTH = 500;
    private static final int MESSAGE_RATE_LIMIT = 5; // сообщений в секунду
    private static final int MESSAGE_HISTORY_LIMIT = 50;


    public List<Message> getRecentMessages() {
        return messageDao.findTop50ByOrderByTimestampDesc();
    }

    public List<Message> getAllMessages() {
        return messageDao.findAllOrderByTimestamp();
    }
    @Transactional
    public Message save (Message message) {
        return messageDao.save(message);
    }


    /**
     * Отправка сообщения в чат стрима
     */
    @Transactional
    public StreamChatMessageDto sendMessage(Long streamId, String userId, String content) {
        log.info("Sending message to stream {} from user {}: {}", streamId, userId, content);

        // 1. Проверка длины сообщения
        if (content == null || content.trim().isEmpty()) {
            throw new ChatException("Message cannot be empty");
        }
        if (content.length() > MAX_MESSAGE_LENGTH) {
            throw new ChatException("Message too long. Max length: " + MAX_MESSAGE_LENGTH);
        }

        // 2. Проверка существования стрима
        StreamChat streamChat = streamChatDao.findById(streamId)
                .orElseThrow(() -> new ChatException("Stream not found"));

        // 3. Проверка пользователя и его прав
        AccountUser accountUser = accountUserDao.findById(Long.valueOf(userId))
                .orElseThrow(() -> new ChatException("User not found"));

        // 4. Проверка бана/мута
        if (accountUser.getStatus() == AccountStatus.BANNED) {
            throw new ChatException("User is banned");
        }
//        if (accountUser.isMuted()) {
//            throw new ChatException("User is muted until " + accountUser.getMutedUntil());
//        }

        // 5. Rate limiting (проверка частоты сообщений)
        LocalDateTime oneSecondAgo = LocalDateTime.now().minusSeconds(1);
        int recentMessages = streamChatMessageDao.countBySenderIdAndTimeStampAfter(userId, oneSecondAgo);
        if (recentMessages >= MESSAGE_RATE_LIMIT) {
            throw new ChatException("Rate limit exceeded. Please slow down.");
        }

        // 6. Сохранение в БД
        StreamChatMessage streamChatMessage =StreamChatMessage.builder()
                .streamId(String.valueOf(streamId))
                .senderId(userId)
                .content(content.trim())
                .timeStamp(LocalDateTime.now())
                .build();

        StreamChatMessage savedMessage = streamChatMessageDao.save(streamChatMessage);

        // 7. Конвертация в DTO
//        MessageDto messageDto = convertToDTO(savedMessage);
        StreamChatMessageDto streamChatMessageDto = convertToDto(savedMessage);

        // 8. Рассылка всем подписчикам
        String destination = String.format("/topic/streams/%d", streamId);
        messagingTemplate.convertAndSend(destination, streamChatMessageDto);

        log.info("Message sent to {}: {}", destination,streamChatMessageDto.getId());

        return streamChatMessageDto;
    }

    /**
     * Получение истории сообщений
     */
    @Transactional(readOnly = true)
    public List<StreamChatMessageDto> getChatHistory(Long streamId, int limit, Long before) {
        log.info("Getting chat history for stream {} with limit {}", streamId, limit);

        if (limit <= 0 || limit > 100) {
            limit = MESSAGE_HISTORY_LIMIT;
        }

        PageRequest pageRequest = PageRequest.of(0, limit, Sort.by("timestamp").descending());

        List<StreamChatMessage> messages;
        if (before != null) {
            // Пагинация "загрузить старее чем messageId"
            messages = streamChatMessageDao.findByStreamIdAndIdLessThanOrderByTimeStampDesc(
                    streamId, before.toString(), pageRequest);
        } else {
            // Первая загрузка
            messages = streamChatMessageDao.findByStreamIdOrderByTimeStampDesc(streamId, pageRequest);
        }

        return messages.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Уведомление о подключении пользователя к чату
     */
    @Transactional
    public void userJoined(Long streamId, String userId) {
        AccountUser accountUser = accountUserDao.findById(Long.valueOf(userId)).orElse(null);
        if (accountUser == null) return;

        StreamChatMessageDto joinMessage = StreamChatMessageDto.builder()
                .id(UUID.randomUUID().toString())
                .streamId(streamId)
                .senderId(userId)
                .username(accountUser.getUsername())
                .content(accountUser.getUsername() + " присоеденился к чату")
                .timestamp(LocalDateTime.now())
                .type(StreamChatStatus.JOIN)
                .build();

        messagingTemplate.convertAndSend(
                String.format("/topic/streams/%d", streamId),
                joinMessage
        );
    }

    /**
     * Уведомление об отключении пользователя
     */
    @Transactional
    public void userLeft(Long streamId, String userId) {
        AccountUser accountUser = accountUserDao.findById(Long.valueOf(userId)).orElse(null);
        if (accountUser == null) return;

        StreamChatMessageDto leaveMessage = StreamChatMessageDto.builder()
                .id(UUID.randomUUID().toString())
                .streamId(streamId)
                .senderId(userId)
                .username(accountUser.getUsername())
                .content(accountUser.getUsername() + " покинул чат")
                .timestamp(LocalDateTime.now())
                .type(StreamChatStatus.LEAVE)
                .build();

        messagingTemplate.convertAndSend(
                String.format("/topic/streams/%d", streamId),
                leaveMessage
        );
    }

    /**
     * Системное сообщение (например, от модератора)
     */
    @Transactional
    public void sendSystemMessage(Long streamId, String content) {
        StreamChatMessageDto systemMessage = StreamChatMessageDto.builder()
                .id(UUID.randomUUID().toString())
                .streamId(streamId)
                .senderId("system")
                .username("System")
                .content(content)
                .timestamp(LocalDateTime.now())
                .type(StreamChatStatus.SYSTEM)
                .build();

        messagingTemplate.convertAndSend(
                String.format("/topic/streams/%d", streamId),
                systemMessage
        );
    }

    private StreamChatMessageDto convertToDto(StreamChatMessage streamChatMessage) {
        return StreamChatMessageDto.builder()
                .id(streamChatMessage.getId().toString())
                .streamId(Long.valueOf(streamChatMessage.getStreamId()))
                .senderId(streamChatMessage.getSenderId())
                .content(streamChatMessage.getContent())
                .timestamp(streamChatMessage.getTimeStamp())
//                .type()
                .build();
    }
}