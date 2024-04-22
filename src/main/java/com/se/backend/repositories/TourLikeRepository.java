package com.se.backend.repositories;

import com.se.backend.models.TourLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface TourLikeRepository extends JpaRepository<TourLike, Long>, JpaSpecificationExecutor<TourLike> {
    List<TourLike> findAllByTourId(Long tourId);
    List<TourLike> findAllByUserId(Long userId);
}
