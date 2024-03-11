package com.se.backend.repositories;

import com.se.backend.models.Tour;
import com.se.backend.models.TourCollection;
import com.se.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TourCollectionRepository extends JpaRepository<TourCollection, Long>, JpaSpecificationExecutor<Tour> {
    List<TourCollection> findAllByUser(User user);
}
