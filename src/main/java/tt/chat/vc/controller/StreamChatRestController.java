//package tt.chat.vc.controller;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.modelmapper.ModelMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
////import tt.chat.vc.dto.MessageDto;
//import tt.chat.vc.dao.ObserverImageDao;
//import tt.chat.vc.dao.StreamChatDao;
//import tt.chat.vc.dao.security.AccountUserDao;
//import tt.chat.vc.dto.MessageDto;
//import tt.chat.vc.dto.StreamChatMessageDto;
//import tt.chat.vc.entity.Message;
//import tt.chat.vc.entity.ObserverImage;
//import tt.chat.vc.entity.StreamChat;
//import tt.chat.vc.entity.StreamChatMessage;
//import tt.chat.vc.entity.security.AccountUser;
//import tt.chat.vc.exception.ChatException;
//import tt.chat.vc.service.ChatService;
//import tt.chat.vc.service.UserService;
//
//import java.security.Principal;
//import java.util.List;
//import java.util.stream.Collectors;
//@Slf4j
//@RestController
//@RequestMapping("/api/chat")
//public class StreamChatRestController {
//    public final ModelMapper modelMapper;
//
//    @Autowired
//    private ChatService chatService;
////    private StreamChatDao streamChatDao;
//
//    @Autowired
//    private  UserService userService;
//    @Autowired
//    private ObserverImageDao observerImageDao;
//    private AccountUserDao accountUserDao;
//
//    public StreamChatRestController(ModelMapper modelMapper) {
//        this.modelMapper = modelMapper;
//    }
//
//    @PostMapping("/message")
//    public StreamChatMessageDto sendMessage(
////            @RequestBody MessageDto messageDto,
//            @RequestBody String content,
//            Principal principal) {
//        String contentDecoder = chatService.decodeMessage(content);
//        AccountUser accountUser = userService.findByUsername(principal.getName());
//        return chatService.sendMessage(123L, accountUser.getId(),contentDecoder.trim());
//    }
//
//
//
////    @PostMapping("/message")
////    public MessageDto sendMessage(
//////            @RequestBody MessageDto messageDto,
////            @RequestBody String content, Principal principal) {
////        Message message = new Message();
////        message.setContent(content);
////        message.setAccountUser(userService.findByUsername(principal.getName()));
////        chatService.save(message);
////        MessageDto messageDto = modelMapper.map(message, MessageDto.class);
////        messageDto.setUserId(message.getAccountUser().getId());
////        return messageDto;
//////        return message;
////    }
//
//
//
//
//
//    @PostMapping("/image")
//    public ObserverImage sendImages() {
//        return observerImageDao.findFirstByObserverId(3L);
//    }
//
//    @GetMapping("/messages")
//    public ResponseEntity<List<StreamChatMessageDto>> getMessages(Long streamId) {
//        List<StreamChatMessage> streamChatMessages = chatService.getRecentStreamChatMessages(streamId);
//        List<StreamChatMessageDto> streamChatMessageDtos = streamChatMessages.stream()
//                .map(msg -> new StreamChatMessageDto(
//                        msg.getId(),
//                        msg.getObserver().getFirstname() + msg.getObserver().getLastname(),
//                        msg.getStreamChat().getId(),
//                        msg.getContent(),
//                        msg.getStreamChatMessageStatus(),
//                        msg.getTimestamp()
//                ))
//                .collect(Collectors.toList());
//        return ResponseEntity.ok(streamChatMessageDtos);
//    }
////    @GetMapping("/messages")
////    public ResponseEntity<List<StreamChatMessage>> getMessages(@PathVariable Long streamId) {
////        List<StreamChatMessage> messages = chatService.getRecentMessages(streamId);
////        return ResponseEntity.ok(messages);
////    }
//}
////
////package tt.chat.vc.controller;
////        import org.springframework.beans.factory.annotation.Autowired;
////        import org.springframework.http.ResponseEntity;
////        import org.springframework.security.core.annotation.AuthenticationPrincipal;
////        import org.springframework.security.core.userdetails.UserDetails;
////        import org.springframework.web.bind.annotation.*;
////        import tt.chat.vc.dto.MessageDto;
////        import tt.chat.vc.entity.Message;
////        import tt.chat.vc.entity.security.AccountUser;
////        import tt.chat.vc.service.ChatService;
////        import tt.chat.vc.service.UserService;
////
////        import java.util.List;
////        import java.util.stream.Collectors;
//
////@RestController
////@RequestMapping("/api/chat")
////public class StreamChatRestController {
////
////    @Autowired
////    private ChatService chatService;
////
////    @Autowired
////    private UserService userService;
////
////    @PostMapping("/message")
////    public ResponseEntity<MessageDto> sendMessage(
////            @RequestBody String content,
////            @AuthenticationPrincipal UserDetails userDetails) {
////
////        AccountUser accountUser = userService.findByUsername(userDetails.getUsername());
////        Message message = chatService.saveMessage(content, accountUser);
////
////        MessageDto messageDto = new MessageDto(
////                message.getId(),
////                message.getContent(),
////                message.getAccountUser().getUsername(),
////                message.getTimestamp()
////        );
////
////        return ResponseEntity.ok(messageDto);
////    }
//
//
////}
//
