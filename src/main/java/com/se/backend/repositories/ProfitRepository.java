package com.se.backend.repositories;

import com.se.backend.models.Profit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfitRepository extends JpaRepository<Profit, Long>, JpaSpecificationExecutor<Profit> {
}
