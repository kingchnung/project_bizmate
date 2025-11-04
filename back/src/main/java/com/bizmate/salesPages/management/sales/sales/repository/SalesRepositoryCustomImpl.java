package com.bizmate.salesPages.management.sales.sales.repository;

import com.bizmate.common.page.PageRequestDTO;
import com.bizmate.salesPages.management.sales.sales.domain.QSales;
import com.bizmate.salesPages.management.sales.sales.domain.Sales;
import com.bizmate.salesPages.management.sales.salesItem.domain.QSalesItem;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class SalesRepositoryCustomImpl implements SalesRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Sales> searchSales(PageRequestDTO pageRequestDTO, Pageable pageable) {
        QSales sales = QSales.sales;
        QSalesItem salesItem = QSalesItem.salesItem;

        // 1) 기본 쿼리 (품목 필터를 위해 SalesItem left join)
        JPAQuery<Sales> query = queryFactory
                .selectFrom(sales)
                .leftJoin(sales.salesItems, salesItem);

        // 2) where 절 동적 생성
        BooleanBuilder builder = buildDynamicWhereClause(pageRequestDTO, sales, salesItem);
        query.where(builder);

        // 3) count(distinct)
        long total = queryFactory
                .select(sales.countDistinct())
                .from(sales)
                .leftJoin(sales.salesItems, salesItem)
                .where(builder)
                .fetchOne();

        // 4) 페이지/정렬 + distinct
        List<Sales> content = query
                .orderBy(sales.salesId.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .distinct()
                .fetch();

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanBuilder buildDynamicWhereClause(PageRequestDTO dto, QSales sales, QSalesItem salesItem) {
        BooleanBuilder builder = new BooleanBuilder();

        String search = dto.getSearch();     // "client" | "project" | "writer" | "item"
        String keyword = dto.getKeyword();
        LocalDate startDate = dto.getStartDate();
        LocalDate endDate = dto.getEndDate();
        BigDecimal minAmount = dto.getMinAmount();
        BigDecimal maxAmount = dto.getMaxAmount();

        // 1) 타입/키워드 검색
        if (StringUtils.hasText(search) && StringUtils.hasText(keyword)) {
            switch (search) {
                case "client" -> builder.and(sales.clientCompany.containsIgnoreCase(keyword));
                case "project" -> builder.and(sales.projectName.containsIgnoreCase(keyword));
                case "writer" -> builder.and(sales.writer.containsIgnoreCase(keyword));
                case "item" -> builder.and(salesItem.itemName.containsIgnoreCase(keyword));
            }
        }

        // 2) 판매일자 기간
        if (Objects.nonNull(startDate) && Objects.nonNull(endDate)) {
            builder.and(sales.salesDate.between(startDate, endDate));
        } else if (Objects.nonNull(startDate)) {
            builder.and(sales.salesDate.eq(startDate));
        }

        // 3) 판매 금액 범위
        if (Objects.nonNull(minAmount) && Objects.nonNull(maxAmount)) {
            builder.and(sales.salesAmount.between(minAmount, maxAmount));
        } else if (Objects.nonNull(minAmount)) {
            builder.and(sales.salesAmount.goe(minAmount));
        } else if (Objects.nonNull(maxAmount)) {
            builder.and(sales.salesAmount.loe(maxAmount));
        }

        // 4) 🔥 발행여부(invoiceIssued) 필터
        if (dto.getInvoiceIssued() != null) {
            builder.and(sales.invoiceIssued.eq(dto.getInvoiceIssued()));
        }

        // 5) 🔥 주문연계(orderId) 필터
        if (StringUtils.hasText(dto.getOrderId())) {
            builder.and(sales.order.orderId.containsIgnoreCase(dto.getOrderId()));
        }

        return builder;
    }
}