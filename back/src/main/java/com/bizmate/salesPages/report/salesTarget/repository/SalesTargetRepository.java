package com.bizmate.salesPages.report.salesTarget.repository;

import com.bizmate.salesPages.report.salesTarget.domain.SalesTarget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.envers.repository.support.EnversRevisionRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SalesTargetRepository extends JpaRepository<SalesTarget, Long>, EnversRevisionRepository<SalesTarget, Long, Integer> {
    Page<SalesTarget> findByTargetYear(Integer year, Pageable pageable);
    Optional<SalesTarget> findByTargetYearAndTargetMonth(Integer targetYear, Integer targetMonth);

    List<SalesTarget> findByTargetYear(Integer year);

    // [신규] 연도별 총 목표액 (연도별 요약)
    @Query("SELECT s.targetYear, SUM(s.targetAmount) FROM SalesTarget s GROUP BY s.targetYear")
    List<Object[]> findYearlyTargetSummary();
}
