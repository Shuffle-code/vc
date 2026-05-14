package tt.chat.vc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tt.chat.vc.dto.StreamChatMessageDto;
import tt.chat.vc.entity.StreamChat;
import tt.chat.vc.entity.enums.StreamChatMessageStatus;
import tt.chat.vc.service.StreamService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
@Slf4j
@Component
@RequiredArgsConstructor
public class SystemMessageScheduler {
    private final SimpMessagingTemplate messagingTemplate;
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
                    .username("Администратор")
                    .timestamp(LocalDateTime.now())
                    .build();
            messagingTemplate.convertAndSend(destination, systemMessageDto);
        }
    }

    private String getRandomMessage() {
        List<String> messages = Arrays.asList(
                "📢 Поделитесь трансляцией с друзьями",
                "⭐ Подпишитесь на канал, чтобы не пропустить новые видео",
                "📢 Отправка сообщений доступна только авторизованным пользователям," +
                        " пройдите регистрацию",
                "🎉 Спасибо, что смотрите трансляцию!",
                "📝 Напишите в чате, о чем хотите узнать подробнее",
                "💬 Задавайте вопросы в чате!",
                "📝 Напишите свои пожелания к оформлению или функционалу сайта"
        );
        return messages.get(new Random().nextInt(messages.size()));
    }
}
