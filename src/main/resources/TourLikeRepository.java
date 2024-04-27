import com.se.backend.models.TourLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TourLikeRepository extends JpaRepository<TourLike, Long>, JpaSpecificationExecutor<TourLike> {
    List<TourLike> findAllByTourId(Long tourId);

    List<TourLike> findAllByUserId(Long userId);

    Optional<TourLike> findByUserIdAndTourId(Long userId, Long tourId);
}
