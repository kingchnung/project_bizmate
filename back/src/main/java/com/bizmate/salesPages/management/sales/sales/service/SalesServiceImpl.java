package com.bizmate.salesPages.management.sales.sales.service;

import com.bizmate.common.page.PageRequestDTO;
import com.bizmate.common.page.PageResponseDTO;
import com.bizmate.hr.security.UserPrincipal;
import com.bizmate.salesPages.management.order.order.domain.Order;
import com.bizmate.salesPages.management.order.order.repository.OrderRepository;
import com.bizmate.salesPages.management.order.orderItem.domain.OrderItem;
import com.bizmate.salesPages.management.sales.sales.domain.Sales;
import com.bizmate.salesPages.management.sales.sales.dto.SalesDTO;
import com.bizmate.salesPages.management.sales.sales.repository.SalesRepository;
import com.bizmate.salesPages.management.sales.salesItem.domain.SalesItem;
import com.bizmate.salesPages.management.sales.salesItem.dto.SalesItemDTO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class SalesServiceImpl implements SalesService {

    private final SalesRepository salesRepository;
    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public String register(SalesDTO salesDTO) {
        LocalDate today = LocalDate.now();
        salesDTO.setSalesIdDate(today);

        // 일자별 시퀀스 생성
        String maxSalesId = salesRepository.findMaxSalesIdBySalesIdDate(today).orElse(null);
        int nextSequence = 1;
        if (maxSalesId != null) {
            try {
                String seqStr = maxSalesId.substring(9);
                nextSequence = Integer.parseInt(seqStr) + 1;
            } catch (Exception ignore) {
                nextSequence = 1;
            }
        }
        String finalSalesId = today.format(DATE_FORMAT) + "-" + String.format("%04d", nextSequence);
        salesDTO.setSalesId(finalSalesId);

        // 작성자/사용자
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserPrincipal userPrincipal) {
            salesDTO.setUserId(userPrincipal.getUsername());
            salesDTO.setWriter(userPrincipal.getEmpName());
        } else {
            throw new IllegalStateException("주문 등록을 위한 사용자 인증 정보를 찾을 수 없습니다. (비정상 접근)");
        }

        // 연결할 주문 조회 (있다면 DTO 기본값 보정)
        Order order = null;
        if (salesDTO.getOrderId() != null && !salesDTO.getOrderId().isEmpty()) {
            order = orderRepository.findByOrderId(salesDTO.getOrderId())
                    .orElseThrow(() -> new NoSuchElementException("Order ID [" + salesDTO.getOrderId() + "]를 찾을 수 없습니다."));

            salesDTO.setProjectId(Optional.ofNullable(salesDTO.getProjectId()).orElse(order.getProjectId()));
            salesDTO.setProjectName(Optional.ofNullable(salesDTO.getProjectName()).orElse(order.getProjectName()));
            salesDTO.setClientId(Optional.ofNullable(salesDTO.getClientId()).orElse(order.getClient().getClientId()));
            salesDTO.setClientCompany(Optional.ofNullable(salesDTO.getClientCompany()).orElse(order.getClient().getClientCompany()));
        }

        // DTO → Entity (order는 스킵되므로 수동 세팅)
        Sales sales = modelMapper.map(salesDTO, Sales.class);
        sales.setOrder(order);

        // SalesItem 구성
        List<SalesItem> finalSalesItems;
        if (salesDTO.getSalesItems() != null && !salesDTO.getSalesItems().isEmpty()) {
            finalSalesItems = salesDTO.getSalesItems().stream()
                    .map(dto -> modelMapper.map(dto, SalesItem.class))
                    .collect(Collectors.toList());
        } else if (order != null) {
            finalSalesItems = new ArrayList<>();
            for (OrderItem oi : order.getOrderItems()) {
                SalesItem si = SalesItem.builder()
                        .itemName(oi.getItemName())
                        .quantity(oi.getQuantity())
                        .unitPrice(oi.getUnitPrice())
                        .unitVat(oi.getUnitVat())
                        .totalAmount(oi.getTotalAmount())
                        .itemNote(oi.getItemNote())
                        .lineNum(oi.getLineNum())
                        .build();
                finalSalesItems.add(si);
            }
        } else {
            finalSalesItems = new ArrayList<>();
        }

        sales.updateSalesItems(finalSalesItems);
        sales.calculateSalesAmount();

        Sales saved = salesRepository.save(sales);
        this.updateOrderStatus(salesDTO.getOrderId());

        return saved.getSalesId();
    }

    @Override
    @Transactional(readOnly = true)
    public SalesDTO get(String salesId) {
        Sales sales = salesRepository.findBySalesId(salesId).orElseThrow();
        SalesDTO dto = modelMapper.map(sales, SalesDTO.class);
        // 🔴 수동 매핑: order.orderId -> dto.orderId
        dto.setOrderId(sales.getOrder() != null ? sales.getOrder().getOrderId() : null);
        return dto;
    }

    @Override
    public void modify(SalesDTO salesDTO) {
        Sales sales = salesRepository.findBySalesId(salesDTO.getSalesId())
                .orElseThrow(() -> new NoSuchElementException("Sales ID [" + salesDTO.getSalesId() + "]을 찾을 수 없습니다."));

        // 상위 필드 변경
        sales.changeClientId(salesDTO.getClientId());
        sales.changeDeploymentDate(salesDTO.getDeploymentDate());
        sales.changeSalesDate(salesDTO.getSalesDate());
        sales.changeSalesNote(salesDTO.getSalesNote());
        sales.changeProjectId(salesDTO.getProjectId());
        sales.changeInvoiceIssued(salesDTO.isInvoiceIssued());

        // 아이템 병합
        List<SalesItemDTO> newItemDto = salesDTO.getSalesItems();
        List<SalesItem> mergedItems = new ArrayList<>();

        if (newItemDto != null) {
            for (SalesItemDTO itemDTO : newItemDto) {
                if (itemDTO.getSalesItemId() != null) {
                    SalesItem existing = sales.getSalesItems().stream()
                            .filter(it -> itemDTO.getSalesItemId().equals(it.getSalesItemId()))
                            .findFirst().orElse(null);
                    if (existing != null) {
                        existing.changeItemName(itemDTO.getItemName());
                        existing.changeQuantity(itemDTO.getQuantity());
                        existing.changeUnitPrice(itemDTO.getUnitPrice());
                        existing.changeUnitVat(itemDTO.getUnitVat());
                        existing.changeItemNote(itemDTO.getItemNote());
                        existing.calculateAmount();
                        mergedItems.add(existing);
                    }
                } else {
                    SalesItem created = modelMapper.map(itemDTO, SalesItem.class);
                    created.calculateAmount();
                    mergedItems.add(created);
                }
            }
        }

        sales.updateSalesItems(mergedItems);
        sales.calculateSalesAmount();

        salesRepository.save(sales);

        this.updateOrderStatus(sales.getOrder() != null ? sales.getOrder().getOrderId() : null);
    }

    @Override
    public void remove(String salesId) {
        Sales sales = salesRepository.findBySalesId(salesId)
                .orElseThrow(() -> new NoSuchElementException("Sales ID [" + salesId + "]를 찾을 수 없습니다."));

        String orderId = (sales.getOrder() != null) ? sales.getOrder().getOrderId() : null;

        // 2. 🔴 변경 지점: 커스텀 쿼리 대신 엔티티 자체를 삭제
        // salesRepository.deleteBySalesId(salesId);
        salesRepository.delete(sales);

        this.updateOrderStatus(orderId);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDTO<SalesDTO> list(PageRequestDTO pageRequestDTO) {
        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(),
                Sort.by("salesId").descending()
        );

        Page<Sales> result = salesRepository.searchSales(pageRequestDTO, pageable);

        List<SalesDTO> dtoList = result.getContent().stream()
                .map(s -> {
                    SalesDTO dto = modelMapper.map(s, SalesDTO.class);
                    dto.setOrderId(s.getOrder() != null ? s.getOrder().getOrderId() : null);
                    return dto;
                })
                .collect(Collectors.toList());

        return PageResponseDTO.<SalesDTO>withAll()
                .dtoList(dtoList)
                .pageRequestDTO(pageRequestDTO)
                .totalCount(result.getTotalElements())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SalesDTO> listByClient(String clientId) {
        return salesRepository.findByClientIdOrderBySalesDateDesc(clientId)
                .stream()
                .map(s -> {
                    SalesDTO dto = modelMapper.map(s, SalesDTO.class);
                    // 💡 수동 매핑: get() 메소드에서 사용한 것과 동일
                    dto.setOrderId(s.getOrder() != null ? s.getOrder().getOrderId() : null);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * 특정 Order ID를 기준으로 연결된 Sales 건들을 분석하여
     * Order의 상태를 (시작전/진행중/완료)로 업데이트합니다.
     * @param orderId 업데이트할 주문 ID
     */
    private void updateOrderStatus(String orderId) {
        if (orderId == null || orderId.isEmpty()) {
            return;
        }

        Order order = orderRepository.findByOrderId(orderId).orElse(null);
        if (order == null) {
            return;
        }

        BigDecimal issuedSalesSum = salesRepository.findSumOfIssuedSalesByOrderId(orderId);
        issuedSalesSum = Optional.ofNullable(issuedSalesSum).orElse(BigDecimal.ZERO);

        BigDecimal orderAmount = Optional.ofNullable(order.getOrderAmount()).orElse(BigDecimal.ZERO);

        // 상태 결정 로직
        // 1. 완료: 발행 합계가 주문액과 일치 (단, 0원짜리 주문은 제외)
        if (orderAmount.compareTo(issuedSalesSum) == 0 && orderAmount.compareTo(BigDecimal.ZERO) > 0) {
            order.changeOrderStatus("완료");
        }
        // 2. 진행중: 발행 합계가 0보다 크지만 주문액과는 다름
        else if (issuedSalesSum.compareTo(BigDecimal.ZERO) > 0) {
            order.changeOrderStatus("진행중");
        }
        // 3. 시작전: 발행 합계가 0 (미발행 건만 있거나, 아무것도 없거나)
        else {
            order.changeOrderStatus("시작전");
        }
    }
}
