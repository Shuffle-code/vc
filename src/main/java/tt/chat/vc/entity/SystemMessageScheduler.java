package tt.chat.vc.entity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tt.chat.vc.dto.StreamChatMessageDto;
import tt.chat.vc.entity.enums.StreamChatMessageStatus;
import tt.chat.vc.service.StreamService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
@Slf4j
@Component
@RequiredArgsConstructor
//@AllArgsConstructor
public class SystemMessageScheduler {
//    @Autowired
    private final SimpMessagingTemplate messagingTemplate;

//    @Autowired
    private final StreamService streamService;

    // Отправка каждые 10 минут
    @Scheduled(fixedDelay = 600000)
    public void sendScheduledMessages() {
        List<StreamChat> activeStreams = streamService.getActiveStreams();

        for (StreamChat streamChat : activeStreams) {
            String destination = "/topic/streams/" + streamChat.getId();

            StreamChatMessageDto systemMessageDto = StreamChatMessageDto.builder()
                    .content(getRandomMessage())
                    .status(StreamChatMessageStatus.SYSTEM)
//                    .senderId(joinMessage.getUserId())
                    .username("Администратор")
                    .timestamp(LocalDateTime.now())
                    .build();

//            StreamChatMessage systemMessage = StreamChatMessage.builder()
//                    .streamChatMessageStatus(StreamChatMessageStatus.SYSTEM)
//                    .content(getRandomMessage())
//                    .timestamp(LocalDateTime.now())
//                    .build();
            log.info(systemMessageDto.getContent() + " " + systemMessageDto.getStatus());
            log.info(destination);
            messagingTemplate.convertAndSend(destination, systemMessageDto);
        }
    }

    private String getRandomMessage() {
        List<String> messages = Arrays.asList(
                "📢 Поделитесь трансляцией с друзьями",
                "⭐ Подпишитесь на канал, чтобы не пропустить новые видео",
                "🎉 Спасибо, что смотрите трансляцию!",
                "📝 Напишите в чате, о чем хотите узнать подробнее",
                "💬 Задавайте вопросы в чате!"
        );
        return messages.get(new Random().nextInt(messages.size()));
    }
}
