package tt.chat.vc.entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import tt.chat.vc.service.OnlineUsersService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class OnlineUsersInterceptor implements HandlerInterceptor {

    @Autowired
    private OnlineUsersService onlineUsersService;

//    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        // Исключаем статические ресурсы и API endpoints
        String uri = request.getRequestURI();
        if (uri.startsWith("/css/") ||
                uri.startsWith("/js/") ||
                uri.startsWith("/images/") ||
                uri.startsWith("/api/") ||
                uri.startsWith("/error")) {
            return true;
        }

        // Получаем или создаем сессию
        request.getSession(true);

        // Обновляем информацию о пользователе
        String sessionId = request.getSession().getId();
        onlineUsersService.addOrUpdateUser(sessionId, request);

        // Добавляем информацию в атрибуты запроса для Thymeleaf
        request.setAttribute("onlineUsersCount",
                onlineUsersService.getOnlineUsersCount());
        request.setAttribute("authenticatedUsersCount",
                onlineUsersService.getAuthenticatedUsersCount());

        return true;
    }
}
