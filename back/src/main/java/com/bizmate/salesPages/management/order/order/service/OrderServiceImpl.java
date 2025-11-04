package com.bizmate.salesPages.management.order.order.service;


import com.bizmate.common.page.PageRequestDTO;
import com.bizmate.common.page.PageResponseDTO;
import com.bizmate.hr.security.UserPrincipal;
import com.bizmate.salesPages.management.order.order.domain.Order;
import com.bizmate.salesPages.management.order.order.dto.OrderDTO;
import com.bizmate.salesPages.management.order.order.repository.OrderRepository;

import com.bizmate.salesPages.management.order.orderItem.domain.OrderItem;
import com.bizmate.salesPages.management.order.orderItem.dto.OrderItemDTO;

import com.bizmate.salesPages.management.sales.sales.repository.SalesRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final SalesRepository salesRepository;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final ModelMapper modelMapper;

    @Override
    public String register(OrderDTO orderDTO) {
        LocalDate today = LocalDate.now();
        orderDTO.setOrderIdDate(today);

        String maxOrderId = orderRepository.findMaxOrderIdByOrderIdDate(today).orElse(null);

        int nextSequence = 1;
        if(maxOrderId != null) {
            try {
                // 기존 ID에서 일련번호 4자리 추출
                String seqStr = maxOrderId.substring(9);
                nextSequence = Integer.parseInt(seqStr) + 1;
            } catch (Exception e){
                // 파싱 오류 발생 시 1로 시작(비정상 케이스)
                nextSequence = 1;
            }
        }

        String datePart = today.format(DATE_FORMAT);
        String sequencePart = String.format("%04d", nextSequence);
        String finalOrderId = datePart + "-" + sequencePart;
        orderDTO.setOrderId(finalOrderId);

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal instanceof UserPrincipal userPrincipal){
            orderDTO.setUserId(userPrincipal.getUsername());
            orderDTO.setWriter(userPrincipal.getEmpName());
        } else {
            throw new IllegalStateException("주문 등록을 위한 사용자 인증 정보를 찾을 수 없습니다. (비정상 접근)");
        }

        Order order = modelMapper.map(orderDTO, Order.class);

        List<OrderItem> newOrderItem = orderDTO.getOrderItems().stream()
                        .map(itemDTO -> modelMapper.map(itemDTO, OrderItem.class))
                                .collect(Collectors.toList());
        order.updateOrderItems(newOrderItem);

        order.calculateOrderAmount();

        Order savedOrder = orderRepository.save(order);
        return savedOrder.getOrderId();
    }

    @Override
    public OrderDTO get(String orderId) {
        Optional<Order> result = orderRepository.findByOrderId(orderId);
        Order order = result.orElseThrow();
        OrderDTO dto = modelMapper.map(order, OrderDTO.class);
        return dto;
    }

    @Override
    public void modify(OrderDTO orderDTO) {
        Optional<Order> result = orderRepository.findByOrderId(orderDTO.getOrderId());
        Order order = result.orElseThrow();

        order.changeClientId(orderDTO.getClientId());
        order.changeOrderDueDate(orderDTO.getOrderDueDate());
        order.changeOrderNote(orderDTO.getOrderNote());
        order.changeProjectId(orderDTO.getProjectId());
        order.changeOrderDate(orderDTO.getOrderDate());

        List<OrderItemDTO> newItemDto = orderDTO.getOrderItems();
        List<OrderItem> mergedItem = new ArrayList<>();

        for(OrderItemDTO itemDTO : newItemDto){
            if(itemDTO.getOrderItemId() != null){
                OrderItem existingItem = order.getOrderItems().stream()
                        .filter(item -> itemDTO.getOrderItemId().equals(item.getOrderItemId()))
                        .findFirst()
                        .orElse(null);

                if(existingItem != null){
                    existingItem.changeItemName(itemDTO.getItemName());
                    existingItem.changeQuantity(itemDTO.getQuantity());
                    existingItem.changeUnitPrice(itemDTO.getUnitPrice());
                    existingItem.changeUnitVat(itemDTO.getUnitVat());
                    existingItem.changeItemNote(itemDTO.getItemNote());

                    existingItem.calculateAmount();
                    mergedItem.add(existingItem);
                }
            } else {
                OrderItem newItem = modelMapper.map(itemDTO, OrderItem.class);

                newItem.calculateAmount();
                mergedItem.add(newItem);
            }
        }

        order.updateOrderItems(mergedItem);
        order.calculateOrderAmount();
        order.changeClientId(orderDTO.getClientId());
        order.changeOrderDueDate(orderDTO.getOrderDueDate());
        order.changeOrderNote(orderDTO.getOrderNote());
        order.changeProjectId(orderDTO.getProjectId());

        orderRepository.save(order);
    }

    @Override
    public void remove(String orderId) {
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order ID [" + orderId + "]를 찾을 수 없습니다."));

        //  🔴 이 주문을 참조하는 Sales가 있는지 확인합니다.
        boolean salesExist = salesRepository.existsByOrderOrderId(orderId);

        if (salesExist) {
            // 🔴 Sales가 존재하면, 예외를 발생시켜 삭제를 중단시킵니다.
            throw new IllegalStateException("이미 '판매' 데이터가 등록된 주문은 삭제할 수 없습니다. 연결된 판매 데이터를 먼저 삭제하세요.");
        }

        orderRepository.delete(order);
    }

    @Override
    public void removeList(List<String> orderIds) {
        // 🔴 삭제할 주문 중 하나라도 Sales에 연결되어 있는지 확인합니다.
        for (String orderId : orderIds) {
            if (salesRepository.existsByOrderOrderId(orderId)) {
                throw new IllegalStateException("삭제 목록에 '판매' 데이터가 등록된 주문(" + orderId + ")이 포함되어 있습니다.");
            }
        }

        List<Order> ordersToDelete = orderRepository.findAllByOrderIdIn(orderIds);
        orderRepository.deleteAllInBatch(ordersToDelete);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<OrderDTO> list(PageRequestDTO pageRequestDTO) {
        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPage() -1,
                pageRequestDTO.getSize(),
                Sort.by("orderId").descending());


        // 기존: Page<Order> result = orderRepository.findAll(pageable);
        // 변경: Querydsl을 사용한 동적 검색 메서드 호출
        Page<Order> result = orderRepository.searchOrders(pageRequestDTO, pageable);

        List<OrderDTO> dtoList = result.getContent().stream().map(
                order -> modelMapper.map(order, OrderDTO.class)).collect(Collectors.toList());

        long totalCount = result.getTotalElements();

        PageResponseDTO<OrderDTO> responseDTO = PageResponseDTO.<OrderDTO>withAll().dtoList(dtoList).pageRequestDTO(pageRequestDTO).totalCount(totalCount).build();

        return responseDTO;
    }
}
