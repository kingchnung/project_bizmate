package com.bizmate.salesPages.management.order.orderItem.controller;

import com.bizmate.salesPages.management.order.order.domain.Order;
import com.bizmate.salesPages.management.order.orderItem.domain.OrderItem;
import com.bizmate.salesPages.management.order.orderItem.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales/orderItem")
@RequiredArgsConstructor
public class OrderItemController {
    private final OrderItemService orderItemService;

    @GetMapping("/all/{orderId}")
    public List<OrderItem> orderItemList(@PathVariable String orderId, OrderItem orderItem, Order order){
        order.setOrderId(orderId);
        orderItem.setOrder(order);
        List<OrderItem> orderItemList = orderItemService.orderItemList(orderItem);
        return orderItemList;
    }

    @PostMapping(value = "/insert", consumes = "application/json", produces = MediaType.APPLICATION_JSON_VALUE)
    public OrderItem orderItemInsert(@RequestBody OrderItem orderItem){
        OrderItem result = orderItemService.orderItemInsert(orderItem);
        return result;
    }

    @PutMapping(value = "/{orderItemId}", consumes = "application/json", produces = MediaType.APPLICATION_JSON_VALUE)
    public OrderItem orderItemUpdate(@PathVariable Long orderItemId, @RequestBody OrderItem orderItem, Order order){
        orderItem.setOrderItemId(orderItemId);
        OrderItem result = orderItemService.orderItemUpdate(orderItem);
        return result;
    }

    @DeleteMapping(value = "/{orderItemId}", produces = MediaType.TEXT_PLAIN_VALUE)
    public void orderItemDelete(@PathVariable Long orderItemId, OrderItem orderItem){
        orderItem.setOrderItemId(orderItemId);
        orderItemService.orderItemDelete(orderItem);
    }
}
