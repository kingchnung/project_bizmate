package com.bizmate.salesPages.management.order.orderItem.service;

import com.bizmate.salesPages.management.order.orderItem.domain.OrderItem;

import java.util.List;

public interface OrderItemService {
    List<OrderItem> orderItemList(OrderItem orderItem);
    OrderItem orderItemInsert(OrderItem orderItem);
    OrderItem orderItemUpdate(OrderItem orderItem);
    void orderItemDelete(OrderItem orderItem);
}
