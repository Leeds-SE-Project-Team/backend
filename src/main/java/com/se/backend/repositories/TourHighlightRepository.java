package com.se.backend.repositories;

import com.se.backend.models.TourHighlight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TourHighlightRepository extends JpaRepository<TourHighlight, Long>, JpaSpecificationExecutor<TourHighlight> {

}



