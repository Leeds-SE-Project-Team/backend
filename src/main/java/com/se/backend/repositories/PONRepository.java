package com.se.backend.repositories;

import com.se.backend.models.PON;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PONRepository extends JpaRepository<PON, Long>, JpaSpecificationExecutor<PON> {
//    Optional<WayPoint> findByLocation(String location);
}