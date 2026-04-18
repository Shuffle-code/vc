package tt.chat.vc.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import tt.chat.vc.entity.security.AccountUser;
import tt.chat.vc.service.ChatService;
import tt.chat.vc.service.UserService;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {
    private final ChatService chatService;
    private final UserService userService;
    @Override
    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication)
            throws IOException{
        String username = authentication.getName();
        AccountUser accountUser = userService.findByUsername(username);
        chatService.userJoined(123L,accountUser.getId());
        if (!request.getHeader("referer").contains("logout")) {
            response.sendRedirect(request.getHeader("referer"));
        } else {
            response.sendRedirect(request.getContextPath() + "/video");

        }
    }

}
