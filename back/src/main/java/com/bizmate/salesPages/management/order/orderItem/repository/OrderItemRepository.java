package com.bizmate.salesPages.management.order.orderItem.repository;

import com.bizmate.salesPages.management.order.order.domain.Order;
import com.bizmate.salesPages.management.order.orderItem.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.orderId = ?1")
    List<OrderItem> orderIdOrderItemList(String orderId);

    @Query("SELECT MAX(oi.lineNum) FROM OrderItem oi WHERE oi.order = :order")
    Optional<Integer> findMaxLineNumByOrder(@Param("order")Order order);
}
