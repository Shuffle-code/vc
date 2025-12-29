package tt.chat.vc.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import tt.chat.vc.service.OnlineUsersService;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private OnlineUsersService onlineUsersService;

    @ModelAttribute
    public void addOnlineUsersCount(HttpServletRequest request) {
        request.setAttribute("onlineUsersCount",
                onlineUsersService.getOnlineUsersCount());
        request.setAttribute("authenticatedUsersCount",
                onlineUsersService.getAuthenticatedUsersCount());
    }
}
