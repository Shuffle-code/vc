package tt.chat.vc.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tt.chat.vc.dao.ObserverDao;
import tt.chat.vc.dao.security.AccountUserDao;
import tt.chat.vc.entity.Observer;
import tt.chat.vc.entity.SessionListener;
import tt.chat.vc.entity.security.AccountUser;
import tt.chat.vc.service.ObserverImageService;
import tt.chat.vc.service.ObserverService;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/observer")
public class ObserverController {
    private final ObserverService observerService;
    private final ObserverDao observerDao;
    private final AccountUserDao accountUserDao;
    private final ObserverImageService observerImageService;
//    @ResponseBody
    @GetMapping("/all")
    public String getObserverList(Model model, HttpSession httpSession){
        httpSession.setAttribute("countObservers", SessionListener.getActiveSessions());
//        httpSession.setAttribute("countPlaying", observerService.countPlaying());
        model.addAttribute("observers", observerService.findAllEnabledUsers(accountUserDao.getAllByEnabled()));
        return "observer/observer-list";
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('observer.create', 'observer.update', 'observer.read')")
    public String showForm(Model model, @RequestParam(name = "id", required = false) Long id) {
        Observer observer;
        if (id != null) {
            observer = observerService.findById(id);
        } else {
            observer = new Observer();
        }
        List<Long> imagesId = new ArrayList<>(observerImageService.uploadMultipleFiles(id));
        model.addAttribute("observerImagesId", imagesId);
        model.addAttribute("observer", observer);
        return "observer/observer-form";
    }

    @GetMapping("/{observerId}")
    @PreAuthorize("hasAnyAuthority('observer.read') || isAnonymous()")
    public String showInfo(Model model, @PathVariable(name = "observerId") Long id) {
        Observer observer;
        if (id != null) {
            observer = observerService.findById(id);
        } else {
            return "redirect:/observer/all";
        }
        List<Long> imagesId = new ArrayList<>(observerImageService.uploadMultipleFiles(id));
        model.addAttribute("observerImagesId", imagesId);
        model.addAttribute("observer", observer);
        return "observer/observer-info";
    }

    @PostMapping("/add")
//    @ResponseBody
    @PreAuthorize("hasAnyAuthority('observer.create', 'observer.update', 'observer.read')")
    public String saveObserver(@Valid Observer observer, @RequestParam("files") MultipartFile[] files,
                              BindingResult bindingResult) {
        if (bindingResult.hasErrors()){
            return "observer/observer-form";
        }
        observerService.save(observer);
        uploadMultipleFiles(files, observerDao.findById(observer.getId()).get().getId());
               return "redirect:/observer/all";
    }
    @GetMapping("/delete/{id}")
    @ResponseBody
    @PreAuthorize("hasAnyAuthority('observer.delete')")
    public String deleteById(@PathVariable(name = "id") Long id) {
        observerService.deleteById(id);
        return "redirect:/observer/all";
    }

    @GetMapping("/status_delete/{id}")

    @PreAuthorize("hasAnyAuthority('observer.delete')")
    public String statusDeleteById(@PathVariable(name = "id") Long id) {
//        observerService.statusDelete(id);
        observerService.userStatusDelete(id);
//        log.info("observer" + id);
        return "redirect:/observer/all";
    }

    @GetMapping("/image_delete/{id}")
    @PreAuthorize("!isAnonymous()")
    public String imageDeleteById(@PathVariable(name = "id") Long id, Model model) {
        Long observerIdByImageId = observerImageService.getObserverIdByImageId(id);
        Observer observer = observerService.findById(observerIdByImageId);
        model.addAttribute("observer", observer);
        observerImageService.deleteImage(id);
        if (observerImageService.countImagesOfObserver(observerIdByImageId) == 0){
            observerImageService.addStartImage(observer);
        }
        model.addAttribute("observerImagesId", observerImageService.uploadMultipleFiles(observerIdByImageId));
        return "observer/observer-form";
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
        log.info(id.toString() + " 'This is here");
        return new byte[]{};
    }
    @PreAuthorize("hasAnyAuthority('observer.read') || isAnonymous()")
    @GetMapping(value = "/images/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] getAllImage(@PathVariable Long id) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ImageIO.write(observerImageService.loadFileAsImageByIdImage(id), "png", byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new byte[]{};
    }

    public void uploadMultipleFiles(@RequestParam("files") MultipartFile[] files, Long id) {
        Arrays.stream(files)
                .map(file -> observerImageService.saveObserverImage(id, file))
                .collect(Collectors.toList());
    }

}




