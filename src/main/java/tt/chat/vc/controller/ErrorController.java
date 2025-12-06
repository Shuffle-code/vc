package tt.chat.vc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class ErrorController {
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "error/access-denied";
    }
}
