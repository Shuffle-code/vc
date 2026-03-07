package tt.chat.vc.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tt.chat.vc.dto.MessageDto;
import tt.chat.vc.dto.StreamChatMessageDto;
import tt.chat.vc.service.ChatService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/streams/{streamId}/chat")
@RequiredArgsConstructor
public class ChatHistoryController {

    private final ChatService chatService;

    /**
     * GET /streams/{streamId}/chat/messages?limit=50&before=messageId
     */
    @GetMapping("/messages")
    public ResponseEntity<List<StreamChatMessageDto>> getChatHistory(
            @PathVariable Long streamId,
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(required = false) Long before) {

        log.info("REST request for chat history: stream={}, limit={}, before={}",
                streamId, limit, before);

        List<StreamChatMessageDto> history = chatService.getChatHistory(streamId, limit, before);
        return ResponseEntity.ok(history);
    }
}
