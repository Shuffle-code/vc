package tt.chat.vc.service.impl;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tt.chat.vc.dao.ChatRoomDao;
import tt.chat.vc.entity.ChatRoom;
import tt.chat.vc.service.ChatRoomService;
import java.util.Optional;
@Service
//@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {
//    @Autowired
    private ChatRoomDao chatRoomDao;
    @Override
    public Optional<String> getChatRoomId(String senderId, String recipientId, boolean createNewRoomIfNotExists){
        return chatRoomDao.findBySenderIdAndRecipientId(senderId, recipientId)
                .map(ChatRoom ::getChatId)
                .or(()->{
                    if (createNewRoomIfNotExists){
                       return Optional.of(createChatId(senderId, recipientId).toString());
                    }
                    return Optional.empty();
                });

    }

    private Object createChatId(String senderId, String recipientId) {
        String chatId = String.format("%S_%S", senderId, recipientId);
        ChatRoom senderRecipient = ChatRoom.builder()
                .chatId(chatId)
                .senderId(senderId)
                .recipientId(recipientId).build();
        ChatRoom recipientSender = ChatRoom.builder()
                .chatId(chatId)
                .recipientId(senderId)
                .senderId(recipientId)
                .build();
        chatRoomDao.save(senderRecipient);
        chatRoomDao.save(recipientSender);
        return chatId;
    }

}
