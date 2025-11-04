package com.bizmate.salesPages.management.sales.sales.repository;

import com.bizmate.common.page.PageRequestDTO;
import com.bizmate.salesPages.management.sales.sales.domain.Sales;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SalesRepositoryCustom {
    /**
     * 동적 검색 조건을 사용하여 판매(Sales) 목록을 페이징 조회
     */
    Page<Sales> searchSales(PageRequestDTO pageRequestDTO, Pageable pageable);
}
