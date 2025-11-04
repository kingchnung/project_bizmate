package com.bizmate.salesPages.management.order.order.repository;

import com.bizmate.common.page.PageRequestDTO;
import com.bizmate.salesPages.management.order.order.domain.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderRepositoryCustom {

    /**
     * 동적 검색 조건을 사용하여 주문 목록을 페이징하여 조회합니다.
     *
     * @param pageRequestDTO 검색 조건 (type, keyword, dates, amounts 등)
     * @param pageable       페이징 정보 (page, size, sort)
     * @return 페이징 처리된 주문 목록
     */
    Page<Order> searchOrders(PageRequestDTO pageRequestDTO, Pageable pageable);
}