package tt.chat.vc.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import tt.chat.vc.entity.Observer;
import tt.chat.vc.entity.security.AccountUser;
import tt.chat.vc.service.ObserverImageService;
import tt.chat.vc.service.UserService;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final ObserverImageService observerImageService;
    @GetMapping
//    @PreAuthorize("hasAuthority('USER')")
    public String userPage(Model model, Principal principal) {
        Observer observer;
        log.info(principal.getName());
        AccountUser accountUser = userService.findByUsername(principal.getName());
        observer = accountUser.getObserver();
        model.addAttribute("accountUser", accountUser);
        List<Long> imagesId = new ArrayList<>(observerImageService.uploadMultipleFiles(observer.getId()));
        model.addAttribute("observerImagesId", imagesId);
        model.addAttribute("observer", observer);
//        model.addAttribute("activeTab", "PersonalArea");
        return "auth/user-info";
    }

    @GetMapping(value = "/image/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    @PreAuthorize("hasAnyAuthority('observer.read') || isAnonymous()")
    public byte[] getImage(@PathVariable Long id) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ImageIO.write(observerImageService.loadFileAsImage(id), "png", byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[]{};
    }

}
