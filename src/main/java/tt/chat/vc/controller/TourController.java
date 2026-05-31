package tt.chat.vc.controller;

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
import tt.chat.vc.dao.security.AccountRoleDao;
import tt.chat.vc.entity.Tour;
import tt.chat.vc.entity.security.AccountRole;
import tt.chat.vc.entity.security.AccountUser;
import tt.chat.vc.service.*;
import jakarta.servlet.http.HttpSession;
import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequestMapping("/tour")
@RequiredArgsConstructor
public class TourController {
    private final ObserverService observerService;
//    private final TourDao tourDao;
    private final UserService userService;
    private final AddressService addressService;
    private final TourService tourService;
    private final TourImageService tourImageService;
    private final AccountRoleDao accountRoleDao;


    public void setHttpSession (HttpSession httpSession){
        httpSession.setAttribute("dateUpcomingTour", tourService.getCurrentTour().getDate());
        httpSession.setAttribute("countPlaying", tourService.countPlayingForTour());
    }

    @GetMapping("/all")
    public String getTourList(Model model) {
//        model.addAttribute("tours", tourService.findAllByStatusEquals(TourStatus.FINISHED));
        model.addAttribute("tours", tourService.findAll());
        return "tour/tour-list";
    }




    public AccountUser changeRoleBack(Principal principal){
        AccountRole roleUser = accountRoleDao.findByName("ROLE_USER");
        AccountUser accountUser = userService.findByUsername(principal.getName());
        Integer userId = accountRoleDao.findRoleIdByUserId(accountUser.getId());
        if (!(userId == 1l)) {
            accountUser.setRoles(Set.of(roleUser));
        }
        return accountUser;
    }

    public AccountUser changeRole(Principal principal){
        AccountRole roleObserver = accountRoleDao.findByName("ROLE_OBSERVER");
        AccountUser accountUser = userService.findByUsername(principal.getName());
        Integer userId = accountRoleDao.findRoleIdByUserId(accountUser.getId());
        if (!(userId == 1l)){
            accountUser.setRoles(Set.of(roleObserver));
        }
        return accountUser;
    }


    @GetMapping("/new")
    @PreAuthorize("hasAnyAuthority('observer.create')")
    public String showForm(Model model, @RequestParam(name = "id", required = false) Long id) {
        Tour tour;
        if (id != null) {
            tour = tourService.findById(id);
//            List<String> images = new ArrayList<>(ObserverImageService.uploadMultipleFilesByObserverId(id));
//            model.addAttribute("observerImages", images);
        } else {
            tour = new Tour();
        }
        model.addAttribute("tourImagesId", tourImageService.uploadMultipleFiles(id));
//        model.addAttribute("observers", observerService.findAll());
        model.addAttribute("addressService", addressService);
        model.addAttribute("tour", tour);
        return "tour/tour-add";
//        return "tour/adding-observers-to-tour";
    }


    @GetMapping("/{tourId}")
    @PreAuthorize("hasAnyAuthority('observer.read') || isAnonymous()")
    public String showInfo(Model model, @PathVariable(name = "tourId") Long id) {
        Tour tour;
        if (id != null) {
            tour = tourService.findById(id);
        } else {
            return "redirect:/tour/all";
        }
        List<Long> imagesId = new ArrayList<>(tourImageService.uploadMultipleFiles(id));
        model.addAttribute("tourImagesId", imagesId);
        model.addAttribute("tour", tour);
        return "tour/tour-info";
    }
    @PostMapping("/new")
    @PreAuthorize("hasAnyAuthority('observer.create', 'observer.update') ")
    public String saveTour(@Valid Tour tour, BindingResult bindingResult, Model model, @RequestParam("files") MultipartFile[] files) {
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> {
                        log.info(error.getDefaultMessage());
            });
            model.addAttribute("addressService", addressService);
            return "tour/tour-add";
        }
        tourService.save(tour);
        uploadMultipleFiles(files, tourService.getIdByTournamentId(tour.getId()));
//        uploadMultipleFiles(files, tourDao.findById(tour.getId()).get().getId());
        return "redirect:/tour/all";
    }

    public void uploadMultipleFiles(@RequestParam("files") MultipartFile[] files, Long id) {
        Arrays.stream(files)
                .map(file -> {
                    try {
                        return tourImageService.saveTourImage(id, file);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }
    @GetMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('observer.delete')")
    public String deleteById(@PathVariable(name = "id") Long id) {
        tourService.deleteById(id);
        return "redirect:/tour/all";
    }

    @GetMapping(value = "/image/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    @PreAuthorize("hasAnyAuthority('observer.read') || isAnonymous()")
    public byte[] getImage(@PathVariable Long id) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ImageIO.write(tourImageService.loadFileAsImage(id), "png", byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[]{};
    }
    @PreAuthorize("hasAnyAuthority('observer.read') || isAnonymous()")
    @GetMapping(value = "/images/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] getAllImage(@PathVariable Long id) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ImageIO.write(tourImageService.loadFileAsImageByIdImage(id), "png", byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[]{};
    }

    @GetMapping(value = "/thumbnail/{tourId}", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    @PreAuthorize("hasAnyAuthority('observer.read') || isAnonymous()")
    public byte[] getThumbnail(@PathVariable Long tourId) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            // Получаем ID изображения-миниатюры по ID тура
            Long thumbnailImageId = tourImageService.getThumbnailImageIdByTourId(tourId);

            if (thumbnailImageId != null) {
                // Если миниатюра есть — отдаём её
                ImageIO.write(tourImageService.loadFileAsImage(thumbnailImageId), "png", byteArrayOutputStream);
                return byteArrayOutputStream.toByteArray();
            } else {
                // Если миниатюры нет — создаём из оригинального фото
                byte[] thumbnail = tourImageService.createAndSaveThumbnail(tourId);
                if (thumbnail != null) {
                    return thumbnail;
                }
                return new byte[]{};
            }
        } catch (Exception e) {
            log.error("Error loading thumbnail for tour {}: {}", tourId, e.getMessage());
            return new byte[]{};
        }
    }

    @GetMapping("/generate-thumbnails")
    @PreAuthorize("hasRole('ADMIN')")
    public String generateAllThumbnails() {
        List<Tour> tours = tourService.findAll();
        int count = 0;
        for (Tour tour : tours) {
            try {
                tourImageService.createAndSaveThumbnail(tour.getId());
                count++;
            } catch (Exception e) {
                log.error("Failed for tour {}: {}", tour.getId(), e.getMessage());
            }
        }
        return "redirect:/tour/all?thumbnailsGenerated=" + count;
    }

    @DeleteMapping("/image_delete/{id}")
    @PreAuthorize("hasAnyAuthority('observer.create')")
    public void imageDelete(@PathVariable(name = "id") Long idImage){
        tourImageService.deleteImageTour(idImage);
        log.error(idImage.toString());
    }

    @GetMapping("/image_delete/{id}")
    @PreAuthorize("!isAnonymous()")
    public String imageDeleteById(@PathVariable(name = "id") Long idImage, Model model) {
        Long tourIdByImageId = tourImageService.getTourIdByImageId(idImage);
        Tour tour  = tourService.findById(tourIdByImageId);
        model.addAttribute("observers", observerService.findAll());
        model.addAttribute("addressService", addressService);
        model.addAttribute("tour", tour);
        tourImageService.deleteImageTour(idImage);
        model.addAttribute("tourImagesId", tourImageService.uploadMultipleFiles(tourIdByImageId));
        return "tour/tour-add";
    }
}




