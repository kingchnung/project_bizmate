package com.bizmate.salesPages.management.order.orderItem.service;

import com.bizmate.salesPages.management.order.order.domain.Order;
import com.bizmate.salesPages.management.order.orderItem.domain.OrderItem;
import com.bizmate.salesPages.management.order.orderItem.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService{
    private final OrderItemRepository orderItemRepository;

    @Override
    public List<OrderItem> orderItemList(OrderItem orderItem) {
        List<OrderItem> orderItemList = orderItemRepository.orderIdOrderItemList(orderItem.getOrder().getOrderId());
        return orderItemList;
    }

    @Override
    public OrderItem orderItemInsert(OrderItem orderItem) {
        Order order = orderItem.getOrder();
        Optional<Integer> maxLineNumOptional = orderItemRepository.findMaxLineNumByOrder(order);

        int newLineNum = maxLineNumOptional
                .map(max -> max+1)
                .orElse(1);

        orderItem.setLineNum(newLineNum);

        OrderItem result = orderItemRepository.save(orderItem);
        return result;
    }

    @Override
    @Transactional
    public OrderItem orderItemUpdate(OrderItem orderItem) {
        Optional<OrderItem> orderItemOptional = orderItemRepository.findById(orderItem.getOrderItemId());
        OrderItem updateOrderItem = orderItemOptional.orElseThrow(
                ()-> new NoSuchElementException("OrderItem ID [" + orderItem.getOrderItemId() + "]를 찾을 수 없습니다.")
        );

        updateOrderItem.changeItemName(orderItem.getItemName());
        updateOrderItem.changeQuantity(orderItem.getQuantity());
        updateOrderItem.changeUnitPrice(orderItem.getUnitPrice());
        updateOrderItem.changeUnitVat(orderItem.getUnitVat());
        updateOrderItem.changeTotalAmount(orderItem.getTotalAmount());
        updateOrderItem.changeItemNote(orderItem.getItemNote());

        OrderItem result = orderItemRepository.save(updateOrderItem);
        return result;
    }

    @Override
    public void orderItemDelete(OrderItem orderItem) {
        orderItemRepository.deleteById(orderItem.getOrderItemId());
    }
}
