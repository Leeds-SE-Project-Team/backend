package com.se.backend.repositories;

import com.se.backend.models.WayPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WayPointRepository extends JpaRepository<WayPoint, Long>, JpaSpecificationExecutor<WayPoint> {
//    Optional<WayPoint> findByLocation(String location);
}