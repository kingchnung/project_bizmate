package com.bizmate.salesPages.management.order.order.service;

import com.bizmate.common.page.PageRequestDTO;
import com.bizmate.common.page.PageResponseDTO;
import com.bizmate.salesPages.management.order.order.dto.OrderDTO;

import java.util.List;

public interface OrderService {
    public String register(OrderDTO orderDTO);
    public OrderDTO get(String orderId);
    public void modify(OrderDTO orderDTO);
    public void remove(String orderId);
    public PageResponseDTO<OrderDTO> list(PageRequestDTO pageRequestDTO);

    public void removeList(List<String> orderIds);
}