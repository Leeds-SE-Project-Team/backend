package com.se.backend.repositories;

import com.se.backend.models.Tour;
import com.se.backend.models.TourCollection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TourCollectionRepository extends JpaRepository<TourCollection, Long>, JpaSpecificationExecutor<Tour> {

}
