package com.se.backend.repositories;


import com.se.backend.models.TourHighlight;
import com.se.backend.models.TourImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TourImageRepository extends JpaRepository<TourImage, Long>, JpaSpecificationExecutor<TourImage> {

    List<TourImage> findByTourHighlight(TourHighlight highlightToDelete);
}
