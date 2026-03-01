package tt.chat.vc.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tt.chat.vc.dao.MessageDao;
//import tt.chat.vc.dto.MessageDto;
import tt.chat.vc.entity.Message;
import tt.chat.vc.entity.Observer;
import tt.chat.vc.entity.security.AccountUser;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatService {

//    @Autowired
    private final MessageDao messageDao;

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


}