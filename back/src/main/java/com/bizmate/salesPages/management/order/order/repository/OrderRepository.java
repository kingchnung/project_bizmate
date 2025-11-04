package com.bizmate.salesPages.management.order.order.repository;

import com.bizmate.salesPages.management.order.order.domain.Order;
import org.springframework.data.envers.repository.support.EnversRevisionRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom, EnversRevisionRepository<Order, Long, Integer> {

    @Query("SELECT MAX(o.orderId) FROM Order o WHERE o.orderIdDate = :orderIdDate")
    Optional<String> findMaxOrderIdByOrderIdDate(@Param("orderIdDate") LocalDate today);

    @Query("SELECT o.orderId FROM Order o ORDER BY o.orderId ASC LIMIT 1")
    Optional<String> findMinOrderId();

    // String 타입 orderId로 엔티티를 조회하는 메서드 추가 (Service에서 사용)
    Optional<Order> findByOrderId(String orderId);

    // String 타입 orderId로 엔티티를 삭제하는 메서드 추가 (Service에서 사용)
    @Modifying
    @Query("DELETE FROM Order o WHERE o.orderId = :orderId")
    void deleteByOrderId(@Param("orderId") String orderId);

    // String 타입 orderId 목록으로 엔티티를 일괄 삭제하는 메서드 추가 (Service에서 사용)
    @Modifying
    @Query("DELETE FROM Order o WHERE o.orderId IN :orderIds")
    void deleteByOrderIdIn(@Param("orderIds") List<String> orderIds);

    List<Order> findAllByOrderIdIn(List<String> orderIds);
}