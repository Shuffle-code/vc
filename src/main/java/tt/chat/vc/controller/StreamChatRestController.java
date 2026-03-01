package tt.chat.vc.controller;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
//import tt.chat.vc.dto.MessageDto;
import tt.chat.vc.dao.ObserverImageDao;
import tt.chat.vc.dto.MessageDto;
import tt.chat.vc.entity.Message;
import tt.chat.vc.entity.ObserverImage;
import tt.chat.vc.service.ChatService;
import tt.chat.vc.service.UserService;

import java.security.Principal;

@RestController
@RequestMapping("/api/chat")

public class StreamChatRestController {
    public final ModelMapper modelMapper;

    @Autowired
    private ChatService chatService;

    @Autowired
    private UserService userService;
    @Autowired
    private ObserverImageDao observerImageDao;

    public StreamChatRestController(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @PostMapping("/message")
    public MessageDto sendMessage(
//            @RequestBody MessageDto messageDto,
            @RequestBody String content, Principal principal) {
        Message message = new Message();
        message.setContent(content);
        message.setAccountUser(userService.findByUsername(principal.getName()));
        chatService.save(message);
        MessageDto messageDto = modelMapper.map(message, MessageDto.class);
        messageDto.setUserId(message.getAccountUser().getId());
        return messageDto;
//        return message;
    }

    @PostMapping("/image")
    public ObserverImage sendImages() {
        return observerImageDao.findFirstByObserverId(3L);
    }

//    @GetMapping("/messages")
//    public ResponseEntity<List<Message>> getMessages() {
//        List<Message> messages = chatService.getRecentMessages();
//        List<Message> messageDtos = messages.stream()
//                .map(msg -> new Message(
//                    msg.getId(),
//                    msg.getContent(),
//                    msg.getAccountUser().getUsername(),
//                    msg.getTimestamp()
//                ))
//                .collect(Collectors.toList());
//
//        return ResponseEntity.ok(messageDtos);
//    }
}
//
//package tt.chat.vc.controller;
//        import org.springframework.beans.factory.annotation.Autowired;
//        import org.springframework.http.ResponseEntity;
//        import org.springframework.security.core.annotation.AuthenticationPrincipal;
//        import org.springframework.security.core.userdetails.UserDetails;
//        import org.springframework.web.bind.annotation.*;
//        import tt.chat.vc.dto.MessageDto;
//        import tt.chat.vc.entity.Message;
//        import tt.chat.vc.entity.security.AccountUser;
//        import tt.chat.vc.service.ChatService;
//        import tt.chat.vc.service.UserService;
//
//        import java.util.List;
//        import java.util.stream.Collectors;
//
//@RestController
//@RequestMapping("/api/chat")
//public class StreamChatRestController {
//
//    @Autowired
//    private ChatService chatService;
//
//    @Autowired
//    private UserService userService;
//
//    @PostMapping("/message")
//    public ResponseEntity<MessageDto> sendMessage(
//            @RequestBody String content,
//            @AuthenticationPrincipal UserDetails userDetails) {
//
//        AccountUser accountUser = userService.findByUsername(userDetails.getUsername());
//        Message message = chatService.saveMessage(content, accountUser);
//
//        MessageDto messageDto = new MessageDto(
//                message.getId(),
//                message.getContent(),
//                message.getAccountUser().getUsername(),
//                message.getTimestamp()
//        );
//
//        return ResponseEntity.ok(messageDto);
//    }
//
//    @GetMapping("/messages")
//    public ResponseEntity<List<MessageDto>> getMessages() {
//        List<Message> messages = chatService.getRecentMessages();
//        List<MessageDto> messageDtos = messages.stream()
//                .map(msg -> new MessageDto(
//                        msg.getId(),
//                        msg.getContent(),
//                        msg.getAccountUser().getUsername(),
//                        msg.getTimestamp()
//                ))
//                .collect(Collectors.toList());
//
//        return ResponseEntity.ok(messageDtos);
//    }
//}

