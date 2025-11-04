package com.bizmate.salesPages.management.collections.repository;

import com.bizmate.common.page.PageRequestDTO;
import com.bizmate.salesPages.management.collections.domain.Collection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CollectionRepositoryCustom {

    /**
     * PageRequestDTO의 검색 조건(type, keyword)을 기반으로
     * 수금 내역을 동적으로 검색하고 페이징합니다.
     *
     * @param pageRequestDTO 검색 조건 (type, keyword) 및 페이징 정보
     * @param pageable 정렬 정보
     * @return 페이징된 수금 내역
     */
    Page<Collection> searchCollection(PageRequestDTO pageRequestDTO, Pageable pageable);
}
