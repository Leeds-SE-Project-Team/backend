package com.se.backend.repositories;

import com.se.backend.models.TourSpot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TourSpotRepository extends JpaRepository<TourSpot, Long>, JpaSpecificationExecutor<TourSpot> {

}
