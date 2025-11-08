package tt.chat.vc.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/video")
public class VideoController {
//    private final ObserverService observerService;
    @GetMapping
    public String video(Model model, HttpSession httpSession) {
        return "video/video";
    }
    @GetMapping("/rules")
    public String rules() {
        return "rules/rules";
    }

    
}




