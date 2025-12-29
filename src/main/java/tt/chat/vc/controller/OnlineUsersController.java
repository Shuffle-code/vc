package tt.chat.vc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import tt.chat.vc.entity.OnlineUser;
import tt.chat.vc.service.OnlineUsersService;

import javax.servlet.http.HttpServletRequest;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class OnlineUsersController {
    @Autowired
    private OnlineUsersService onlineUsersService;

    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * Панель администратора с онлайн пользователями
     */
    @GetMapping("/online-users")
    public String showOnlineUsers(Model model, HttpServletRequest request) {
        // Проверяем авторизацию администратора
        if (!isAdmin(request)) {
            return "redirect:/login";
        }

        // Получаем всех онлайн пользователей и сортируем по времени последней активности
        var users = onlineUsersService.getAllOnlineUsers();
        users.sort(Comparator.comparing(OnlineUser::getLastActivity).reversed());

        model.addAttribute("users", users);
        model.addAttribute("totalUsers", users.size());
        model.addAttribute("authenticatedUsers",
                onlineUsersService.getAuthenticatedUsersCount());
        model.addAttribute("anonymousUsers",
                users.size() - onlineUsersService.getAuthenticatedUsersCount());
        model.addAttribute("pageVisits", onlineUsersService.getPageVisits());
        model.addAttribute("browserStats", onlineUsersService.getBrowserStats());
        model.addAttribute("usersByIp", onlineUsersService.getUsersByIp());
        model.addAttribute("formatter", formatter);
        model.addAttribute("currentTime",
                java.time.LocalDateTime.now().format(formatter));

        return "admin/online-users";
    }

    /**
     * Статистика в реальном времени (JSON для AJAX)
     */
    @GetMapping("/online-stats")
    @ResponseBody
    public Map<String, Object> getOnlineStats() {
        return onlineUsersService.getStatistics();
    }

    /**
     * Панель мониторинга (главная страница админки)
     */
    @GetMapping("/dashboard")
    public String showDashboard(Model model, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return "redirect:/login";
        }

        model.addAttribute("onlineCount",
                onlineUsersService.getOnlineUsersCount());
        model.addAttribute("authCount",
                onlineUsersService.getAuthenticatedUsersCount());
        model.addAttribute("stats", onlineUsersService.getStatistics());

        return "admin/dashboard";
    }

    /**
     * Простая проверка администратора
     */
    private boolean isAdmin(HttpServletRequest request) {
        Object admin = request.getSession().getAttribute("isAdmin");
        return admin != null && (boolean) admin;
    }
}
