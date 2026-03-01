//package tt.chat.vc.controller;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//
//import com.example.streamapp.model.ChatMessage;
//import com.example.streamapp.model.StreamInfo;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//@Controller
//public class StreamChatController {
//    private List<ChatMessage> chatMessages = new ArrayList<>();
//    private StreamInfo streamInfo = new StreamInfo();
//
//    @GetMapping("/stream")
//    public String streamPage(Model model) {
//        // Инициализация данных для трансляции
//        streamInfo.setTitle("Моя трансляция");
//        streamInfo.setViewerCount(24);
//        streamInfo.setIsLive(true);
//        streamInfo.setCamera1Name("Основная камера");
//        streamInfo.setCamera2Name("Вторая камера");
//
//        model.addAttribute("streamInfo", streamInfo);
//        model.addAttribute("chatMessages", chatMessages);
//        model.addAttribute("newMessage", new ChatMessage());
//
//        return "stream";
//    }
//
//    @PostMapping("/stream/send-message")
//    public String sendMessage(@RequestParam String messageText,
//                              @RequestParam String sender) {
//        if (messageText != null && !messageText.trim().isEmpty()) {
//            ChatMessage message = new ChatMessage();
//            message.setSender(sender);
//            message.setText(messageText.trim());
//            message.setTimestamp(LocalDateTime.now());
//
//            chatMessages.add(message);
//
//            // Ограничиваем историю сообщений
//            if (chatMessages.size() > 50) {
//                chatMessages.remove(0);
//            }
//        }
//
//        return "redirect:/stream";
//    }
//
//    @PostMapping("/stream/start")
//    public String startStream() {
//        streamInfo.setIsLive(true);
//        return "redirect:/stream";
//    }
//
//    @PostMapping("/stream/stop")
//    public String stopStream() {
//        streamInfo.setIsLive(false);
//        return "redirect:/stream";
//    }
//}
//
//
////@Controller
////public class CameraController {
////    @GetMapping("/camera")
////    public String getCamera(Model model) {
////        StreamInfo streamInfo = new StreamInfo();
////        streamInfo.setLive(true); // или false
////        model.addAttribute("streamInfo", streamInfo);
////        model.addAttribute("streamUrl", "https://rtsp.ru/embed/HdaN53Fz/");
////        return "camera";
////    }
////}