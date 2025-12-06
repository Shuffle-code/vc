package tt.chat.vc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tt.chat.vc.dao.MessageDao;
import tt.chat.vc.entity.Message;
import tt.chat.vc.entity.security.AccountUser;

import java.util.List;

@Service
public class ChatService {

//    @Autowired
    private MessageDao messageDao;

    public Message saveMessage(String content, AccountUser accountUser) {
        Message message = new Message(content, accountUser);
        return messageDao.save(message);
    }

    public List<Message> getRecentMessages() {
        return messageDao.findTop50ByOrderByTimestampDesc();
    }

    public List<Message> getAllMessages() {
        return messageDao.findAllOrderByTimestamp();
    }
}