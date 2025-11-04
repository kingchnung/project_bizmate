package com.bizmate.salesPages.management.collections.repository;

import com.bizmate.common.page.PageRequestDTO;
import com.bizmate.salesPages.client.domain.QClient;
import com.bizmate.salesPages.management.collections.domain.Collection;
import com.bizmate.salesPages.management.collections.domain.QCollection;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
public class CollectionRepositoryImpl extends QuerydslRepositorySupport implements CollectionRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public CollectionRepositoryImpl(JPAQueryFactory queryFactory) {
        super(Collection.class);
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<Collection> searchCollection(PageRequestDTO pageRequestDTO, Pageable pageable) {

        QCollection collection = QCollection.collection;
        QClient client = QClient.client;

        JPAQuery<Collection> query = queryFactory
                .selectFrom(collection)
                .leftJoin(collection.client, client)
                .where(createSearchConditions(pageRequestDTO, collection, client)); // 👈 3. client 전달

        // 1. Count 쿼리 실행
        long total = query.fetchCount();

        // 2. 실제 데이터 쿼리
        List<Collection> content = getQuerydsl().applyPagination(pageable, query).fetch();

        return new PageImpl<>(content, pageable, total);
    }

    /**
     * DTO의 검색 조건(startDate, endDate, minAmount, maxAmount, search, keyword)을
     * 기반으로 Querydsl의 BooleanBuilder (WHERE 조건)를 생성합니다.
     */
    private BooleanBuilder createSearchConditions(PageRequestDTO pageRequestDTO, QCollection collection, QClient client) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        // --- 🔽 4. 날짜 기간 검색 (startDate, endDate) 🔽 ---
        LocalDate startDate = pageRequestDTO.getStartDate();
        LocalDate endDate = pageRequestDTO.getEndDate();

        if (startDate != null && endDate != null) {
            booleanBuilder.and(collection.collectionDate.between(startDate, endDate));
        } else if (startDate != null) {
            booleanBuilder.and(collection.collectionDate.goe(startDate)); // >=
        } else if (endDate != null) {
            booleanBuilder.and(collection.collectionDate.loe(endDate)); // <=
        }

        // --- 🔽 5. 금액 범위 검색 (minAmount, maxAmount) 🔽 ---
        BigDecimal minAmount = pageRequestDTO.getMinAmount();
        BigDecimal maxAmount = pageRequestDTO.getMaxAmount();

        if (minAmount != null && maxAmount != null) {
            booleanBuilder.and(collection.collectionMoney.between(minAmount, maxAmount));
        } else if (minAmount != null) {
            booleanBuilder.and(collection.collectionMoney.goe(minAmount)); // >=
        } else if (maxAmount != null) {
            booleanBuilder.and(collection.collectionMoney.loe(maxAmount)); // <=
        }

        // --- 🔽 6. 텍스트 키워드 검색 (search, keyword) 🔽 ---
        String searchType = pageRequestDTO.getSearch();
        String keyword = pageRequestDTO.getKeyword();

        if (StringUtils.hasText(searchType) && StringUtils.hasText(keyword)) {

            switch (searchType) {
                case "c": // 거래처명
                    booleanBuilder.and(client.clientCompany.containsIgnoreCase(keyword));
                    break;
                case "w": // 작성자
                    booleanBuilder.and(collection.writer.containsIgnoreCase(keyword));
                    break;
                case "n": // 비고
                    booleanBuilder.and(collection.collectionNote.containsIgnoreCase(keyword));
                    break;
                case "all": // 전체 (거래처명 OR 작성자 OR 비고)
                    booleanBuilder.and(
                            client.clientCompany.containsIgnoreCase(keyword)
                                    .or(collection.writer.containsIgnoreCase(keyword))
                                    .or(collection.collectionNote.containsIgnoreCase(keyword))
                    );
                    break;
            }
        }

        return booleanBuilder;
    }
}