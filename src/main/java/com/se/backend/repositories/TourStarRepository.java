package com.se.backend.repositories;

import com.se.backend.models.TourStar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TourStarRepository extends JpaRepository<TourStar, Long>, JpaSpecificationExecutor<TourStar> {
    List<TourStar> findAllByTourId(Long tourId);
    List<TourStar> findAllByUserId(Long userId);
}
