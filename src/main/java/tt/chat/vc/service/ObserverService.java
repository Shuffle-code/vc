package tt.chat.vc.service;

import tt.chat.vc.dao.ObserverDao;
import tt.chat.vc.entity.Observer;
import tt.chat.vc.entity.ObserverImage;
import tt.chat.vc.entity.enums.Status;
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

import java.io.File;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ObserverService {
    private final ObserverDao observerDao;
    private final ObserverImageService observerImageService;
    @Transactional(propagation = Propagation.NEVER, isolation = Isolation.DEFAULT)
    public Long count() {
        return observerDao.count();
    }

    @Transactional(propagation = Propagation.NEVER, isolation = Isolation.DEFAULT)
    public Integer countPlaying() {
        return observerDao.findAllByStatus(Status.ACTIVE).size();
    }

    public Observer saveNew (Observer observer) {
        if (observer.getId() != null) {
            Optional<Observer> observerFromDBOptional = observerDao.findById(observer.getId());
            if (observerFromDBOptional.isPresent()) {
                Observer observerFromDB = observerFromDBOptional.get();
                observerFromDB.setFirstname(observer.getFirstname());
                observerFromDB.setPatronymic(observer.getPatronymic());
                observerFromDB.setYearOfBirth(observer.getYearOfBirth());
                observerFromDB.setStatus(observer.getStatus());
                return observerDao.save(observerFromDB);
            }
        }
        observer.setId(null);
        return observerDao.save(observer);
    }

    public Observer getObserverIdByIdTtw(String ratingTtw){
        return findById(observerDao.getObserverIdByIdTtw(ratingTtw));
    }

    public List<String> getIdTtw(){
        return observerDao.getIdTtw();
    }
    public Long maxId(){
        return observerDao.maxId();
    }

// сохранение для Post /observer/add
    @Transactional
    public Observer save (Observer observer, MultipartFile multipartFile) {
        if (observer.getId() != null) {
        observerDao.findById(observer.getId()).ifPresent(
                    (p) -> observer.setVersion(p.getVersion()));
        }
        if (multipartFile != null && !multipartFile.isEmpty()) {
            String pathToSavedFile = observerImageService.save(multipartFile);
            ObserverImage observerImage = ObserverImage.builder()
                    .path(pathToSavedFile)
                    .observer(observer)
                    .build();
            observer.addImage(observerImage);
        }
//        Observer currentObserver = observer;
//        observer.setId(null);
        return observerDao.save(observer);
    }

    public Observer updateRatingTtw(Observer observer, BigDecimal bigDecimal) {
        Optional<Observer> observerFromDBOptional = observerDao.findById(observer.getId());
        if (observerFromDBOptional.isPresent()) {
            Observer observerFromDB = observerFromDBOptional.get();
            observerFromDB.setRttw(bigDecimal);
            return observerDao.save(observerFromDB);
        }
        return observerDao.save(observer);
    }


    @Transactional
    public Observer save(final Observer observer) {
        return save(observer, (MultipartFile) null);
    }


    public List<Observer> findAll() {
        return observerDao.findAll();
    }

    public List<Observer> findAllActive() {
        return observerDao.findAllByStatus(Status.ACTIVE);
    }

    public void deleteById(Long id) {
        try {
            observerDao.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            log.error(e.getMessage());
        }
    }

    public void statusDelete(Long id) {
        Optional<Observer> Observer = observerDao.findById(id);
        Observer.ifPresent(p -> {
            p.setStatus(Status.DELETED);
            observerDao.save(p);
        });
    }

    public void disable(Long id) {
        Optional<Observer> Observer = observerDao.findById(id);
        Observer.ifPresent(p -> {
            p.setStatus(Status.DISABLE);
            observerDao.save(p);
        });
    }
    public List<Observer> findAll(int page, int size) {
        return observerDao.findAllByStatus(Status.ACTIVE, PageRequest.of(page, size));
    }
    @Transactional(readOnly = true)
    public List<Observer> findAllActiveSortedById() {
        return observerDao.findAllByStatus(Status.ACTIVE, Sort.by(Sort.Direction.DESC,"id"));
    }
//    @Transactional(readOnly = true)
//    public List<Janitor> findAllActiveSortedByRating() {
//        return janitorDao.findAllByStatus(Status.ACTIVE, Sort.by(Sort.Direction.DESC,"rating"));
//    }
//    @Transactional(readOnly = true)
//    public List<Janitor> findAllDisableSortedByRating() {
//        return janitorDao.findAllByStatus(Status.DISABLE, Sort.by(Sort.Direction.DESC,"rating"));
//    }
//    @Transactional(readOnly = true)
//    public List<Janitor> findAllNotActiveSortedByRating() {
//        return janitorDao.findAllByStatus(Status.NOT_ACTIVE, Sort.by(Sort.Direction.DESC,"rating"));
//    }

    public List<Observer> addListForMainPage(){
        List<Observer> observers = Stream
                .of( findAll())
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        return observers;
    }

    @Transactional(readOnly = true)
    public List<Observer> findAllSortedById(int page, int size) {
        return observerDao.findAllByStatus(Status.ACTIVE, PageRequest.of(page, size, Sort.by("id")));
    }


    public String stringObserver(Observer observer){
        String str = observer.getFirstname() + observer.getLastname();
        return str;
    }
    @Transactional(readOnly = true)
    public Observer findById(Long id) {
    return observerDao.findById(id).orElse(null);
}

}
