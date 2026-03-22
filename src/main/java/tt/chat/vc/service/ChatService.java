package tt.chat.vc.service;

        import lombok.RequiredArgsConstructor;
        import lombok.extern.slf4j.Slf4j;
        import org.springframework.data.domain.PageRequest;
        import org.springframework.data.domain.Sort;
        import org.springframework.messaging.simp.SimpMessagingTemplate;
        import org.springframework.stereotype.Service;
        import org.springframework.transaction.annotation.Transactional;
        import tt.chat.vc.dao.MessageDao;
        import tt.chat.vc.dao.ObserverDao;
        import tt.chat.vc.dao.StreamChatDao;
        import tt.chat.vc.dao.StreamChatMessageDao;
        import tt.chat.vc.dao.security.AccountUserDao;
        import tt.chat.vc.dto.StreamChatMessageDto;
        import tt.chat.vc.entity.Message;
        import tt.chat.vc.entity.Observer;
        import tt.chat.vc.entity.StreamChat;
        import tt.chat.vc.entity.StreamChatMessage;
        import tt.chat.vc.entity.enums.StreamChatMessageStatus;
        import tt.chat.vc.entity.enums.StreamChatStatus;
        import tt.chat.vc.entity.security.AccountUser;
        import tt.chat.vc.entity.security.enums.AccountStatus;
        import tt.chat.vc.exception.ChatException;

        import java.net.URLDecoder;
        import java.nio.charset.StandardCharsets;
        import java.time.LocalDateTime;
        import java.util.List;
        import java.util.Optional;
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
    private final ObserverDao observerDao;

    // Константы ограничений
    private static final int MAX_MESSAGE_LENGTH = 500;
    private static final int MESSAGE_RATE_LIMIT = 5; // сообщений в секунду
    private static final int MESSAGE_HISTORY_LIMIT = 40;


    public List<Message> getRecentMessages() {
        return messageDao.findTop10ByOrderByTimestampDesc();
    }

    public List<StreamChatMessage> getRecentMessages(Long streamId) {
        return streamChatMessageDao.findLast10MessagesByStreamId(streamId);
    }

    public String decodeMessage(String encodedMessage) {
        try {
            return URLDecoder.decode(encodedMessage, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return encodedMessage;
        }
    }

    public List<StreamChatMessage> getRecentStreamChatMessages(Long streamId) {
        return streamChatMessageDao.findLast10MessagesByStreamId(streamId);
    }

//    public List<Message> getAllMessages() {
//        return messageDao.findAllOrderByTimestamp();
//    }

    public List<Message> getAllMessages() {
        return messageDao.findAll();
    }

    @Transactional
    public Message save (Message message) {
        return messageDao.save(message);
    }


    /**
     * Отправка сообщения в чат стрима
     */
    @Transactional
    public StreamChatMessageDto sendMessage(Long streamId, Long userId, String content) {
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

        Observer observer = observerDao.findByAccountUserId(accountUser.getId());

        // 4. Проверка бана/мута
        if (accountUser.getStatus() == AccountStatus.BANNED) {
            throw new ChatException("User is banned");
        }
//        if (accountUser.isMuted()) {
//            throw new ChatException("User is muted until " + accountUser.getMutedUntil());
//        }

        // 5. Rate limiting (проверка частоты сообщений)
        LocalDateTime oneSecondAgo = LocalDateTime.now().minusSeconds(1);
        int recentMessages = streamChatMessageDao.countBySenderIdAndStreamIdAndTimestampAfter(userId, streamId, oneSecondAgo);
        if (recentMessages >= MESSAGE_RATE_LIMIT) {
            throw new ChatException("Rate limit exceeded. Please slow down.");
        }
        String cleanContent = content.replaceAll("_csrf=[^&\\s]+&message=?", "").trim();
        // 6. Сохранение в БД
        StreamChatMessage streamChatMessage =StreamChatMessage.builder()
                .streamChat(streamChat)
                .observer(observer)
                .content(cleanContent)
                .streamChatMessageStatus(StreamChatMessageStatus.USER)
                .timestamp(LocalDateTime.now())
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

//        if (limit <= 0 || limit > 100) {
//            limit = MESSAGE_HISTORY_LIMIT;
//        }
//        PageRequest pageRequest = PageRequest.of(0, limit, Sort.by("timestamp").descending());

        List<StreamChatMessage> messages;
        if (before != null) {
            // Пагинация "загрузить старее чем messageId"
//            messages = streamChatMessageDao.findByStreamIdAndIdLessThanOrderByTimestampDesc(
//                    streamId, before.toString(), pageRequest);

            messages = streamChatMessageDao.findAll();
        } else {
//            messages = streamChatMessageDao.findAll();
            // Первая загрузка
            messages = streamChatMessageDao.findLast10MessagesByStreamId(streamId);
//            findByStreamIdOrderByTimestampDesc(streamId, pageRequest);
        }
        return messages.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Уведомление о подключении пользователя к чату
     */
    @Transactional
    public void userJoined(Long streamId, Long userId) {
        Optional<AccountUser> accountUser = accountUserDao.findById(userId);
        Observer observer = observerDao.findByAccountUserId(accountUser.get().getId());
        StreamChatMessageDto leaveMessage = StreamChatMessageDto.builder()
                .id(System.currentTimeMillis())
                .streamId(streamId)
                .senderId(observer.getId())
                .username(observer.getFirstname() + " " + observer.getLastname())
                .content(observer.getFirstname() + " покинул чат")
                .timestamp(LocalDateTime.now())
                .status(StreamChatMessageStatus.JOIN)
                .build();
        messagingTemplate.convertAndSend(
                String.format("/topic/streams/%d", streamId),
                leaveMessage);
    }

    /**
     * Уведомление об отключении пользователя
     */
    @Transactional
    public void userLeft(Long streamId, Long userId) {
        AccountUser accountUser = accountUserDao.findById(Long.valueOf(userId)).orElse(null);
        if (accountUser == null) return;

//        StreamChatMessageDto joinNotification = StreamChatMessageDto.builder()
//                .content(" присоединился к чату")
//                .status(StreamChatMessageStatus.JOIN)
//                .senderId(joinMessage.getUserId())
//                .username(username)
//                .timestamp(LocalDateTime.now())
//                .build();

        StreamChatMessageDto leaveMessage = StreamChatMessageDto.builder()
                .id(System.currentTimeMillis())
                .streamId(streamId)
                .senderId(userId)
                .username(accountUser.getUsername())
                .content(accountUser.getUsername() + " покинул чат")
                .timestamp(LocalDateTime.now())
                .status(StreamChatMessageStatus.LEAVE)
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
                .id(Long.valueOf(String.valueOf(UUID.randomUUID())))
                .streamId(streamId)
                .username("System")
                .content(content)
                .timestamp(LocalDateTime.now())
                .status(StreamChatMessageStatus.SYSTEM)
                .build();

        messagingTemplate.convertAndSend(
                String.format("/topic/streams/%d", streamId),
                systemMessage
        );
    }

    private StreamChatMessageDto convertToDto(StreamChatMessage streamChatMessage) {
        return StreamChatMessageDto.builder()
                .id(streamChatMessage.getId())
                .streamId(Long.valueOf(streamChatMessage.getStreamChat().getId()))
                .senderId(streamChatMessage.getObserver().getId())
                .content(streamChatMessage.getContent())
                .timestamp(streamChatMessage.getTimestamp())
                .status(streamChatMessage.getStreamChatMessageStatus())
                .username(streamChatMessage.getObserver().getFirstname() + " " + streamChatMessage.getObserver().getLastname())
                .build();
    }
}