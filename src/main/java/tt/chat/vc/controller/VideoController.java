package tt.chat.vc.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import tt.chat.vc.entity.OnlineUser;
import tt.chat.vc.entity.SessionListener;
import tt.chat.vc.service.ObserverService;
//import tt.chat.vc.service.OnlineUsersService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/video")
public class VideoController {
    private final ObserverService observerService;
//    @Autowired
//    private OnlineUsersService onlineUsersService;
    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("HH:mm:ss");
    private final LocalDate currentDate = LocalDate.now();
    @GetMapping
    public String video(Model model, HttpSession httpSession) {
        httpSession.setAttribute("countObservers", SessionListener.getActiveSessions());
//        httpSession.setAttribute("countObservers", observerService.countObservers().toString());
        httpSession.setAttribute("data", currentDate);
        model.addAttribute("onlineCount", SessionListener.getOnlineUsers().size());
//                observerService.countAll().toString());
        return "video/video";
    }
    @GetMapping("/rules")
    public String rules() {
        return "rules/rules";
    }

    
}




