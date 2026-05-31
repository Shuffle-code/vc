package tt.chat.vc.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import tt.chat.vc.entity.SessionListener;
import tt.chat.vc.entity.enums.Status;
import tt.chat.vc.entity.security.AccountUser;
import tt.chat.vc.entity.security.enums.AccountStatus;
import tt.chat.vc.service.ChatService;
import tt.chat.vc.service.TourService;
import tt.chat.vc.service.UserService;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {
    private final ChatService chatService;
    private final UserService userService;
    private static final Long START_TOUR_ID = 0L;
    @Override
    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication)
            throws IOException{
        String username = authentication.getName();
        AccountUser accountUser = userService.findByUsername(username);
        userService.updateUserStatus(accountUser, AccountStatus.OFFLINE, Status.OFFLINE);
        chatService.userLeft(getCurrentTourIdFromReferer(request), accountUser.getId());
        if (!request.getHeader("referer").contains("logout")) {
            response.sendRedirect(request.getHeader("referer"));
        } else {
            response.sendRedirect(request.getContextPath() + "/video");

        }
    }

    private Long getCurrentTourIdFromReferer(HttpServletRequest request) {
        String referer = request.getHeader("referer");
        if (referer != null && referer.matches(".*/video/\\d+.*")) {
            // Извлекаем число из URL
            Pattern pattern = Pattern.compile("/video/(\\d+)");
            Matcher matcher = pattern.matcher(referer);
            if (matcher.find()) {
                return Long.parseLong(matcher.group(1));
            }
        }
        return START_TOUR_ID;
    }

}
