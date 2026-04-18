package tt.chat.vc.service;

import lombok.RequiredArgsConstructor;
import org.apache.el.stream.Stream;
import org.springframework.stereotype.Service;
import tt.chat.vc.dao.StreamChatDao;
import tt.chat.vc.entity.StreamChat;

import java.util.List;
@Service
@RequiredArgsConstructor
public class StreamService {
    private final StreamChatDao streamChatDao;
    public List<StreamChat> getActiveStreams() {
        List<StreamChat> streamChatList = streamChatDao.findAll();
        return streamChatList; // TODO: 11.04.2026
    }
}
