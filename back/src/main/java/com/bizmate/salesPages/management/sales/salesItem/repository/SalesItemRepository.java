package com.bizmate.salesPages.management.sales.salesItem.repository;

import com.bizmate.salesPages.management.sales.sales.domain.Sales;
import com.bizmate.salesPages.management.sales.salesItem.domain.SalesItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SalesItemRepository extends JpaRepository<SalesItem, Long> {
    @Query("SELECT si FROM SalesItem si WHERE si.sales.salesId = ?1")
    List<SalesItem> salesIdSalesItemList(String salesId);

    @Query("SELECT MAX(si.lineNum) FROM SalesItem si WHERE si.sales = :sales")
    Optional<Integer> findMaxLineNumByOrder(@Param("sales")Sales sales);
}
