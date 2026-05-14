package tt.chat.vc.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import tt.chat.vc.dao.TourDao;
import tt.chat.vc.entity.Tour;
import tt.chat.vc.entity.SessionListener;
import tt.chat.vc.entity.security.AccountUser;
import tt.chat.vc.service.*;

import java.security.NoSuchAlgorithmException;
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
    private final TourDao tourDao;
    private final UserService userService;
    private final ChatService chatService;
    private final TourImageService tourImageService;
    private final TourService tourService;
    private final RtspTokenService rtspTokenService;
    private final ZonedDateTime nowNovosibirsk = ZonedDateTime.now(ZoneId.of("Asia/Novosibirsk"));
    private final LocalDate currentDate = nowNovosibirsk.toLocalDate();
    private static final String START_URL = "https://cdn.fast.jwp.services/v1/channel/0_zetjamzo_Hplllvlc/manifest/3.m3u8";
    private static final Long START_TOUR_ID = 0L;
    @GetMapping
    public String video(Model model, HttpSession httpSession, Principal principal) {
        setAttributeHttpSessionAndModel(httpSession, model, principal, START_TOUR_ID, START_URL);
        model.addAttribute("singleCameraMode", true);
        model.addAttribute("activeCamera", 2);
        return "video/video";
    }
    @GetMapping("/{tourId}")
    public String videoCurrent(Model model, HttpSession httpSession, Principal principal,
                               @PathVariable(name = "tourId") Long tourId) {
        Tour currentTour = tourDao.findTourById(tourId);
        String videoUrlCam1 = currentTour.getVideoUrlCam1();
        String videoUrlCam2 = currentTour.getVideoUrlCam2();
        setAttributeHttpSessionAndModel(httpSession, model, principal, tourId, videoUrlCam1);
        model.addAttribute("videoUrlCam2", videoUrlCam2);
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
    public void setAttributeHttpSessionAndModel (HttpSession httpSession, Model model, Principal principal,
                                                 Long currentTourId, String videoUrl){
        httpSession.setAttribute("countObservers", SessionListener.getActiveSessions());
        httpSession.setAttribute("data", currentDate);
        model.addAttribute("videoUrl", videoUrl);
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

    @GetMapping("/rtsp-stream")
    public String getRtspStream(Model model) throws NoSuchAlgorithmException {
        String streamUrl = rtspTokenService.getStreamUrl();
        model.addAttribute("streamUrl", streamUrl);
        return "rtsp-player";
    }

//    private String getVideoUrlForTour(Long tourId) {
//        switch(true) {
//            case "CAM1":
//                return "http://localhost:1984/stream.html?src=cam1";
//            case "CAM2":
//                return "https://cdn.fast.jwp.services/v1/channel/0_zetjamzo_Hplllvlc/manifest/3.m3u8";
//            default:
//                return "https://vk.com/video_ext.php?oid=-106879986&id=456252669&hash=5d26418cb04251ab&hd";
//        }
//    }
}




