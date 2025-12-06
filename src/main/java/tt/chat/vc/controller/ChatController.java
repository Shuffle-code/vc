package tt.chat.vc.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import tt.chat.vc.dto.MessageDto;
import tt.chat.vc.entity.Message;
import tt.chat.vc.entity.security.AccountUser;
import tt.chat.vc.service.ChatService;
import tt.chat.vc.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private UserService userService;

    @PostMapping("/message")
    public ResponseEntity<MessageDto> sendMessage(
            @RequestBody String content,
            @AuthenticationPrincipal UserDetails userDetails) {

        AccountUser accountUser = userService.findByUsername(userDetails.getUsername());
        Message message = chatService.saveMessage(content, accountUser);

        MessageDto messageDto = new MessageDto(
            message.getId(),
            message.getContent(),
            message.getAccountUser().getUsername(),
            message.getTimestamp()
        );

        return ResponseEntity.ok(messageDto);
    }

    @GetMapping("/messages")
    public ResponseEntity<List<MessageDto>> getMessages() {
        List<Message> messages = chatService.getRecentMessages();
        List<MessageDto> messageDtos = messages.stream()
                .map(msg -> new MessageDto(
                    msg.getId(),
                    msg.getContent(),
                    msg.getAccountUser().getUsername(),
                    msg.getTimestamp()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(messageDtos);
    }
}