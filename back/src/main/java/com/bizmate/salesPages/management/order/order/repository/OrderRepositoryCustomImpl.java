package com.bizmate.salesPages.management.order.order.repository;

import com.bizmate.common.page.PageRequestDTO;
import com.bizmate.salesPages.management.order.order.domain.Order;
import com.bizmate.salesPages.management.order.order.domain.QOrder;
import com.bizmate.salesPages.management.order.orderItem.domain.QOrderItem;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryCustomImpl implements OrderRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Order> searchOrders(PageRequestDTO pageRequestDTO, Pageable pageable) {
        QOrder order = QOrder.order;
        QOrderItem orderItem = QOrderItem.orderItem;

        // 1. 기본 쿼리 생성 (품목명 검색을 위해 OrderItem을 leftJoin)
        JPAQuery<Order> query = queryFactory
                .selectFrom(order)
                .leftJoin(order.orderItems, orderItem);

        // 2. 동적 WHERE 조건 생성
        BooleanBuilder builder = buildDynamicWhereClause(pageRequestDTO, order, orderItem);
        query.where(builder);

        // 3. 전체 카운트 쿼리 (distinct 적용)
        // (주의: join으로 인해 count 쿼리도 distinct가 필요)
        JPAQuery<Long> countQuery = queryFactory
                .select(order.countDistinct()) // 중복 제거 카운트
                .from(order)
                .leftJoin(order.orderItems, orderItem)
                .where(builder);

        long total = countQuery.fetchOne();

        // 4. 페이징 및 정렬 적용 (distinct 적용)
        List<Order> content = query
                .orderBy(order.orderId.desc()) // 정렬 조건 (필요시 pageable에서 가져오도록 수정)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .distinct() // Join으로 인한 Order 중복 제거
                .fetch();

        return new PageImpl<>(content, pageable, total);
    }

    /**
     * PageRequestDTO를 기반으로 동적 Where절 (BooleanBuilder)을 생성합니다.
     */
    private BooleanBuilder buildDynamicWhereClause(PageRequestDTO dto, QOrder order, QOrderItem orderItem) {
        BooleanBuilder builder = new BooleanBuilder();

        String search = dto.getSearch();
        String keyword = dto.getKeyword();
        LocalDate startDate = dto.getStartDate();
        LocalDate endDate = dto.getEndDate();
        BigDecimal minAmount = dto.getMinAmount();
        BigDecimal maxAmount = dto.getMaxAmount();

        // 1. 타입별 키워드 검색
        if (StringUtils.hasText(search) && StringUtils.hasText(keyword)) {
            switch (search) {
                case "client":
                    builder.and(order.client.isNotNull()
                            .and(order.client.clientCompany.containsIgnoreCase(keyword)));
                    break;
                case "project":
                    builder.and(order.projectName.containsIgnoreCase(keyword));
                    break;
                case "writer":
                    builder.and(order.writer.containsIgnoreCase(keyword));
                    break;
                case "item":
                    // Left Join된 OrderItem의 품목명에서 검색
                    builder.and(orderItem.itemName.containsIgnoreCase(keyword));
                    break;
            }
        }

        // 2. 주문일자/기간 검색
        if (Objects.nonNull(startDate) && Objects.nonNull(endDate)) {
            // 기간 검색 (endDate는 23:59:59까지 포함하는 것으로 간주)
            builder.and(order.orderDate.between(startDate, endDate));
        } else if (Objects.nonNull(startDate)) {
            // 단일 일자 검색
            builder.and(order.orderDate.eq(startDate));
        }

        // 3. 금액별 검색
        if (Objects.nonNull(minAmount) && Objects.nonNull(maxAmount)) {
            // 금액 범위 검색
            builder.and(order.orderAmount.between(minAmount, maxAmount));
        } else if (Objects.nonNull(minAmount)) {
            // 최소 금액 이상
            builder.and(order.orderAmount.goe(minAmount));
        } else if (Objects.nonNull(maxAmount)) {
            // 최대 금액 이하
            builder.and(order.orderAmount.loe(maxAmount));
        }

        return builder;
    }
}