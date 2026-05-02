package tt.chat.vc.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import tt.chat.vc.entity.Tour;
import tt.chat.vc.entity.SessionListener;
import tt.chat.vc.entity.security.AccountUser;
import tt.chat.vc.service.*;
import java.security.Principal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/video")
public class VideoController {
    private final UserService userService;
    private final ChatService chatService;
    private final TourImageService tourImageService;
    private final TourService tourService;
    private final ZonedDateTime nowNovosibirsk = ZonedDateTime.now(ZoneId.of("Asia/Novosibirsk"));
    private final LocalDate currentDate = nowNovosibirsk.toLocalDate();

    @GetMapping
    public String video(Model model, HttpSession httpSession, Principal principal) {
        Long currentTourId = Optional.ofNullable(tourService.findFirstByStatus())
                .map(Tour::getId)
                .orElse(null);
        setAttributeHttpSessionAndModel(httpSession, model, principal, currentTourId);
        return "video/video";
    }
    @GetMapping("/{tourId}")
    public String videoCurrent(Model model, HttpSession httpSession, Principal principal,
                               @PathVariable(name = "tourId") Long tourId) {
//        Long currentTourId = Optional.ofNullable(tourService.findById(tourId))
//                .map(Tour::getId)
//                .orElse(null);
        setAttributeHttpSessionAndModel(httpSession, model, principal, tourId);
        return "video/video";
    }
    public Long getCurrentUserId (Principal principal){
        return Optional.ofNullable(principal)
                .map(Principal::getName)
                .map(userService::findByUsername)
                .map(AccountUser::getId)
                .orElse(-1L);
    }
    public List<Long> getImagesId(Long tourId) {
        return Optional.ofNullable(tourId)
                .map(tourImageService::uploadMultipleFiles)
                .orElse(new ArrayList<>());
    }
    public void setAttributeHttpSessionAndModel (HttpSession httpSession, Model model, Principal principal, Long currentTourId){
        httpSession.setAttribute("countObservers", SessionListener.getActiveSessions());
        httpSession.setAttribute("data", currentDate);
        model.addAttribute("onlineCount", SessionListener.getAuthenticatedUserCount());
        model.addAttribute( "currentUserId", getCurrentUserId(principal));
        model.addAttribute( "streamId", tourService.getIdByTournamentId(currentTourId));
        model.addAttribute("tours", tourService.getCurrentTourByStatusActive());
        model.addAttribute("messages", chatService.getRecentStreamChatMessages(currentTourId));
        model.addAttribute("images", getImagesId(currentTourId));
    }
    @GetMapping("/rules")
    public String rules() {
        return "rules/rules";
    }
}




