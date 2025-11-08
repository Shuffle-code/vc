package tt.chat.vc.dao;

import tt.chat.vc.entity.Observer;
import tt.chat.vc.entity.enums.Status;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ObserverDao extends JpaRepository<Observer, Long> {
    List<Observer> findAllByStatus(Status status);
    List<Observer> findAllByStatus(Status status, Pageable pageable);
    List<Observer> findAllByStatus(Status status, Sort sort);
    @Query(value = "SELECT MAX(id) FROM observer ", nativeQuery = true)
    Long maxId();

//    @Query(value = "SELECT ID_TTWR FROM nsk_tt.player where ID_TTWR != 'null' & ID_TTWR != ''", nativeQuery = true)
//    List<String> getIdTtw();

    @Query(value = "SELECT ID FROM nsk_tt.player where ID_TTWR = :idTtw", nativeQuery = true)
    Long getObserverIdByIdTtw(String idTtw);
    @Query(value = "SELECT ID_TTWR FROM nsk_tt.player where ID_TTWR != 'null' & ID_TTWR != ''", nativeQuery = true)
    List<String> getIdTtw();

    Optional<Observer> findByLastname(String title);
//    @Query(value = "SELECT ID FROM nsk_tt.player where ID_TTWR = :idTtw", nativeQuery = true)
//    Long getObserverIdByIdTtw(String idTtw);
}
