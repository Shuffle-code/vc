package tt.chat.vc.dao;

import org.springframework.data.repository.query.Param;
import tt.chat.vc.entity.Observer;
import tt.chat.vc.entity.enums.Status;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tt.chat.vc.entity.security.enums.AccountStatus;

import java.util.List;
import java.util.Optional;

public interface ObserverDao extends JpaRepository<Observer, Long> {
    List<Observer> findAllByStatus(Status status);
    List<Observer> findAllByStatus(Status status, Pageable pageable);
    List<Observer> findAllByStatus(Status status, Sort sort);
    @Query(value = "SELECT MAX(id) FROM observer ", nativeQuery = true)
    Long maxId();
    @Query(value = "SELECT ID FROM observer where ID_TTWR = :idTtw", nativeQuery = true)
    Long getObserverIdByIdTtw(String idTtw);
    @Query(value = "SELECT ID_TTWR FROM account_user where ID_TTWR != 'null' & ID_TTWR != ''", nativeQuery = true)
    List<String> getIdTtw();
//    List<Observer> findAllById (List<Long> ids, Sort sort);
    Optional<Observer> findByLastname(String name);
//    @Query(value = "SELECT * FROM observer WHERE account_user_id = :userId", nativeQuery = true)
//    Observer findByAccountUserId(@Param("userId") Long userId);

    @Query(value = "SELECT * FROM observer WHERE id = :userId", nativeQuery = true)
    Observer findByAccountUserId(@Param("userId") Long userId);

    @Query(value = "SELECT OBSERVER_ID FROM account_user where ENABLED = true", nativeQuery = true)
    List<Long> getAllByEnabled();

    @Query(value = "SELECT OBSERVER_ID FROM account_user WHERE STATUS = :status", nativeQuery = true)
    List<Long> getAllIdObserverByStatus(@Param("status") AccountStatus accountStatus);

    @Query("SELECT au.observer FROM AccountUser au WHERE au.status = :status")
    List<Observer> getAllObserverByStatus(@Param("status") AccountStatus accountStatus);

    @Query("SELECT o FROM Observer o ORDER BY CASE WHEN o.status = 'ONLINE' THEN 0 ELSE 1 END, o.id DESC")
    List<Observer> findAllSortedByStatusOnlineFirst();
}
