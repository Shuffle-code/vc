//package tt.chat.vc.service.impl;
//
//import org.springframework.stereotype.Service;
//import tt.chat.vc.dao.ChatMessageDao;
//import tt.chat.vc.entity.ChatMessage;
//import tt.chat.vc.service.ChatMessageService;
//import tt.chat.vc.service.ChatRoomService;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//public class ChatMessageServiceImpl implements ChatMessageService {
//    private ChatMessageDao chatMessageDao;
//    private ChatRoomService chatRoomService;
//    @Override
//    public ChatMessage saveChatMessage(ChatMessage chatMessage) {
//        var chatId = chatRoomService.getChatRoomId(
//                chatMessage.getSenderId(),
//                chatMessage.getRecipientId(),
//                true
//        ).orElseThrow();
//        chatMessage.setChatId(chatId);
//        chatMessageDao.save(chatMessage);
//        return chatMessage;
//    }
//
//    @Override
//    public List<ChatMessage> findChatMessages(String senderId, String recipientId) {
//        var chatId = chatRoomService.getChatRoomId(senderId, recipientId,false);
//        return chatId.map(chatMessageDao :: findByChatId).orElse(new ArrayList<>());
//    }
//}
