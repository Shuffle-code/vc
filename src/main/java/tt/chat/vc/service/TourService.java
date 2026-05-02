package tt.chat.vc.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tt.chat.vc.dao.StreamChatDao;
import tt.chat.vc.dao.TourDao;
import tt.chat.vc.entity.StreamChat;
import tt.chat.vc.entity.Tour;
import tt.chat.vc.entity.TourImage;
import tt.chat.vc.entity.enums.Status;
import tt.chat.vc.entity.enums.TourStatus;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TourService {
    private final TourDao tourDao;
    private final TourImageService tourImageService;
    private final StreamChatDao streamChatDao;
    private final int oneDay = 86400000;
    private Date now = new Date(System.currentTimeMillis() - oneDay);

    @Transactional(propagation = Propagation.NEVER, isolation = Isolation.DEFAULT)
    public Long count() {
        return tourDao.count();
    }

    public Tour save(Tour tour, File file) {
        if (tour.getId() != null) {
            Optional<Tour> tourFromDBOptional = tourDao.findById(tour.getId());
            if (tourFromDBOptional.isPresent()) {
                Tour tourFromDB = tourFromDBOptional.get();
                tourFromDB.setDate(tour.getDate());
                tourFromDB.setTitle(tour.getTitle());
                tourFromDB.setAddress(tour.getAddress());
                tourFromDB.setAmountPlayers(tour.getAmountPlayers());
                return tourDao.save(tourFromDB);
            }
        }
        return tourDao.save(tour);
    }

    @Transactional
    public Tour save(Tour tour, MultipartFile multipartFile) {
        if (tour.getId() != null) {
            tourDao.findById(tour.getId()).ifPresent(
                    (p) -> tour.setVersion(p.getVersion())
            );
        }
        if (multipartFile != null && !multipartFile.isEmpty()) {
            String pathToSavedFile = tourImageService.save(multipartFile);
            TourImage tourImage = TourImage.builder()
                    .path(pathToSavedFile)
                    .tour(tour)
                    .build();
            tour.addImage(tourImage);
            log.info(tourImage.getPath());
            tourImageService.deleteStartImage(tourImage);
        }
        else if (tourImageService.getAllIdImagesByTourId(tour.getId()).isEmpty()){
            tourImageService.addStartImage(tour);
        }
        Tour savedTour = tourDao.save(tour);
        if (!streamChatDao.existsByTournamentId(savedTour.getId())) {
            StreamChat streamChat = StreamChat.builder()
                    .title(savedTour.getTitle())
                    .ownerId(savedTour.getId())
                    .tournamentId(savedTour.getId())
                    .build();
            streamChatDao.save(streamChat);
        }
        return savedTour;
    }

    @Transactional
    public Tour save(final Tour tour) {
        return save(tour, (MultipartFile) null);
    }


    public List<Tour> findAll() {
        List<Tour> all = tourDao.findAll();
        return all;
    }

    public void deleteById(Long id) {
        try {
            tourDao.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            log.error(e.getMessage());
        }
    }
    public List<Tour> findAll(int page, int size) {
        return tourDao.findAllByStatus(Status.ACTIVE, PageRequest.of(page, size));
    }

    @Transactional(readOnly = true)
    public List<Tour> findAllActiveSortedById() {
        return tourDao.findAllByStatus(Status.ACTIVE, Sort.by(Sort.Direction.DESC, "id"));
    }

    @Transactional(readOnly = true)
    public List<Tour> findAllActiveSortedByRating() {
        return tourDao.findAllByStatus(Status.ACTIVE, Sort.by(Sort.Direction.DESC, "rating"));
    }

    @Transactional(readOnly = true)
    public List<Tour> findAllSortedById(int page, int size) {
        return tourDao.findAllByStatus(Status.ACTIVE, PageRequest.of(page, size, Sort.by("id")));
    }

    @Transactional(readOnly = true)
    public List<Tour> findAllSortedByData() {
        return tourDao.findAll(Sort.by(Sort.Direction.DESC, "date"));
    }

    @Transactional(readOnly = true)
    public List<Tour> findAllByStatusNot(TourStatus tourStatus) {
        return tourDao.findAllByStatusNot(tourStatus, Sort.by(Sort.Direction.DESC, "date"));
    }


    @Transactional(readOnly = true)
    public List<Tour> findAllByStatusEquals(TourStatus tourStatus) {
        return tourDao.findAllByStatusEquals(tourStatus, Sort.by(Sort.Direction.DESC, "date"));
    }

    @Transactional(readOnly = true)
    public Tour findById(Long id) {
        return tourDao.findById(id).orElse(null);
    }

    public Long getCurrentTourForTranslationId() {
        Long currentTourId;
        Tour byDateEquals = tourDao.findByDateEquals(now);
        if (byDateEquals == null) {
            currentTourId = tourDao.findFirstByDateAfter(now).getId();
        } else currentTourId = byDateEquals.getId();
        return currentTourId;
    }

    public Tour getCurrentTourForTranslation() {
        Tour currentTour;
        Tour byDateEquals = tourDao.findByDateEquals(now);
        if (byDateEquals == null) {
            currentTour = tourDao.findFirstByDateAfter(now);
        } else currentTour = byDateEquals;
        return currentTour;
    }

    public BigDecimal countPlayingForTour() {
        return tourDao.findById(getCurrentTourId()).get().getAmountPlayers();
    }

    public Long getCurrentTourId() {
        return tourDao.findFirstByDateAfter(now).getId();
    }
    public Tour getCurrentTour(){
        return tourDao.findFirstByDateAfter(now);
    }
    public List<Tour> getCurrentTourByStatusActive(){
        return tourDao.findToursByStatus(TourStatus.ACTIVE);
    }
    public Tour findFirstByStatus(){
        return tourDao.findFirstByStatus(TourStatus.ACTIVE).orElse(null);
    }
    public Long getIdByTournamentId(Long tourId){
        return streamChatDao.findStreamChatByTournamentId(tourId).getId();
    }

    public StreamChat findStreamByTourId(Long tourId){
        return streamChatDao.findStreamChatByTournamentId(tourId);
    }
}

