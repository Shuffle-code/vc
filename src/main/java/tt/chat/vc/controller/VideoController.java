package tt.chat.vc.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
//import tt.chat.vc.entity.OnlineUser;
import tt.chat.vc.entity.SessionListener;
import tt.chat.vc.entity.security.AccountUser;
import tt.chat.vc.service.ChatService;
import tt.chat.vc.service.ObserverImageService;
import tt.chat.vc.service.ObserverService;
import tt.chat.vc.service.UserService;
//import tt.chat.vc.service.OnlineUsersService;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/video")
public class VideoController {
    private final UserService userService;
    private final ChatService chatService;
    private final ObserverImageService observerImageService;
//    private static final DateTimeFormatter formatter =
//            DateTimeFormatter.ofPattern("HH:mm:ss");
    private final LocalDate currentDate = LocalDate.now();
    private static final Long STREAM_ID = 123L;
    private static final Long ADMIN_ID = 5L;

    @GetMapping
    public String video(Model model, HttpSession httpSession, Principal principal) {
        Long currentUserId = Optional.ofNullable(principal)
                .map(Principal::getName)
                .map(username -> userService.findByUsername(username))
                .map(AccountUser::getId)
                .orElse(-1L);
        httpSession.setAttribute("countObservers", SessionListener.getActiveSessions());
        httpSession.setAttribute("data", currentDate);
//        httpSession.setAttribute("onlineCount", SessionListener.getAuthenticatedUserCount());
        model.addAttribute("onlineCount", SessionListener.getAuthenticatedUserCount());
        model.addAttribute( "currentUserId", currentUserId);
        model.addAttribute( "streamId", STREAM_ID);
        model.addAttribute("messages", chatService.getRecentStreamChatMessages(STREAM_ID));
        List<Long> imagesId = new ArrayList<>(observerImageService.uploadMultipleFiles(ADMIN_ID));
        model.addAttribute("images", imagesId);
        return "video/video";
    }
    @GetMapping("/rules")
    public String rules() {
        return "rules/rules";
    }

    
}




