package com.se.backend.repositories;

import com.se.backend.models.Profit;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfitRepository extends JpaRepository<Profit, Long>, JpaSpecificationExecutor<Profit> {
    @Query("SELECT YEAR(p.buyTime) as year, SUM(p.amount) as totalAmount FROM Profit p GROUP BY YEAR(p.buyTime) ORDER BY YEAR(p.buyTime)")
    List<Object[]> yearlyRevenueSum();
}
