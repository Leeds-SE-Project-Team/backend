package com.se.backend.repositories;

import com.se.backend.models.Profit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfitRepository extends JpaRepository<Profit, Long>, JpaSpecificationExecutor<Profit> {
//    @Query(value = "SELECT DATE_FORMAT(p.buy_time, '%X-%V') as yearWeek, SUM(p.amount) as totalAmount FROM profit p GROUP BY yearWeek ORDER BY yearWeek", nativeQuery = true)
//    List<Object[]> weeklyRevenueSum();
}
