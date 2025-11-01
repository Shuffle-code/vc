package tt.chat.vc.dao;

import tt.chat.vc.entity.ObserverImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ObserverImageDao extends JpaRepository<ObserverImage, Long> {

    ObserverImage findFirstByObserverId(Long id);

    @Query(value = "SELECT observer_image.path FROM observer_image WHERE observer_image.observer_id = :id LIMIT 1", nativeQuery = true)
    String findImageNameByObserverId(@Param("id") Long id);

    @Query(value = "SELECT observer_image.path FROM observer_image WHERE observer_image.id = :id LIMIT 1", nativeQuery = true)
    String findImageNameByImageId(@Param("id") Long id);

    @Query(value = "SELECT observer_image.id FROM observer_image WHERE observer_image.path = :path LIMIT 1", nativeQuery = true)
    Long findImageIdByPath(String path);

    @Query(value = "SELECT observer_image.id from observer_image WHERE observer_image.observer_id = :id", nativeQuery = true)
    List<Long> findAllIdImagesByObserverId(@Param("id") Long id);

    @Query(value = "SELECT MAX(id) FROM observer_image ", nativeQuery = true)
    Long maxId();

    @Query(value = "SELECT COUNT(observer_id) FROM observer_image WHERE observer_image.observer_id = :id", nativeQuery = true)
    Long count(Long id);

    @Override
    void delete(ObserverImage observerImage);

    @Override
    void deleteById(Long aLong);
    @Query(value = "SELECT observer_image.observer_id from observer_image WHERE observer_image.id = :id", nativeQuery = true)
    Long findObserverIdByImageId(Long id);

}
