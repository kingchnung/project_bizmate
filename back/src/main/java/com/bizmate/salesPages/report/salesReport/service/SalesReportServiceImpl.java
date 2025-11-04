package com.bizmate.salesPages.report.salesReport.service;


import com.bizmate.common.page.PageRequestDTO;
import com.bizmate.common.page.PageResponseDTO;
import com.bizmate.salesPages.client.domain.Client;
import com.bizmate.salesPages.client.repository.ClientRepository;
import com.bizmate.salesPages.management.collections.repository.CollectionRepository;
import com.bizmate.salesPages.management.sales.sales.repository.SalesRepository;
import com.bizmate.salesPages.report.salesReport.dto.*;
import com.bizmate.salesPages.report.salesTarget.domain.SalesTarget;
import com.bizmate.salesPages.report.salesTarget.repository.SalesTargetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 리포트 서비스는 대부분 읽기 전용
public class SalesReportServiceImpl implements SalesReportService {

    // 🔽 리포트에 필요한 모든 Repository 주입
    private final SalesRepository salesRepository;
    private final CollectionRepository collectionRepository;
    private final SalesTargetRepository salesTargetRepository;
    private final ClientRepository clientRepository;

    /**
     * [신규] 거래처별 매출 현황 (월별, 페이징)
     */
    @Override
    public PageResponseDTO<ClientSalesStatusDTO> getClientSalesStatus(
            PageRequestDTO pageRequestDTO, Integer year, Integer month) {

        // 1. 목표액 조회 (월별 또는 연간)
        BigDecimal totalTargetAmount;
        if (month != null && month > 0) {
            // 월별 목표
            totalTargetAmount = salesTargetRepository.findByTargetYearAndTargetMonth(year, month)
                    .map(SalesTarget::getTargetAmount)
                    .orElse(BigDecimal.ZERO);
        } else {
            // 연간 목표
            totalTargetAmount = salesTargetRepository.findByTargetYear(year).stream()
                    .map(SalesTarget::getTargetAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        // 2. '전체 기간' 미수금 맵 조회 (로직 동일)
        Map<String, ClientReceivablesDTO> receivablesMap = this.getClientReceivablesSummaryInternal().stream()
                .collect(Collectors.toMap(ClientReceivablesDTO::getClientId, dto -> dto));

        // 3. '월별' 또는 '연간' 매출 맵 조회
        Map<String, BigDecimal> salesMap;
        if (month != null && month > 0) {
            // [기존] 월별 매출
            String yearMonth = String.format("%d-%02d", year, month);
            salesMap = salesRepository.findClientSalesSummaryByYearMonth(yearMonth).stream()
                    .collect(Collectors.toMap(ClientSalesSummary::getClientId, ClientSalesSummary::getTotalSalesAmount));
        } else {
            // [신규] 연간 매출
            salesMap = salesRepository.findClientSalesSummaryByYear(String.valueOf(year)).stream()
                    .collect(Collectors.toMap(ClientSalesSummary::getClientId, ClientSalesSummary::getTotalSalesAmount));
        }
        // 4. '거래처(Client)' 목록을 페이징하여 조회
        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(),
                Sort.by("clientCompany").ascending()
        );
        Page<Client> clientPage = clientRepository.findAll(pageable);

        // 5. Client 목록을 ClientSalesStatusDTO로 변환
        Page<ClientSalesStatusDTO> dtoPage = clientPage.map(client -> {
            String clientId = client.getClientId();

            BigDecimal sales = salesMap.getOrDefault(clientId, BigDecimal.ZERO); // 월간 또는 연간 매출
            BigDecimal outstandingBalance = Optional.ofNullable(receivablesMap.get(clientId))
                    .map(ClientReceivablesDTO::getOutstandingBalance)
                    .orElse(BigDecimal.ZERO);

            // 목표 대비 달성률 (월간 목표 또는 연간 목표 기준)
            BigDecimal achievementRatio = BigDecimal.ZERO;
            if (totalTargetAmount.compareTo(BigDecimal.ZERO) > 0) {
                achievementRatio = sales.divide(totalTargetAmount, 4, RoundingMode.HALF_UP);
            }

            return ClientSalesStatusDTO.builder()
                    .clientId(clientId)
                    .clientCompany(client.getClientCompany())
                    .monthlySalesAmount(sales) // DTO 필드명은 재활용
                    .outstandingBalance(outstandingBalance)
                    .achievementRatio(achievementRatio)
                    .build();
        });

        // 6. PageResponseDTO로 포장하여 반환
        return PageResponseDTO.<ClientSalesStatusDTO>withAll()
                .dtoList(dtoPage.getContent())
                .pageRequestDTO(pageRequestDTO)
                .totalCount(dtoPage.getTotalElements())
                .build();
    }

    /**
     * [신규] 기간별 매출 현황 (연도별)
     */
    @Override
    public List<PeriodSalesStatusDTO> getPeriodSalesStatus(Integer year) {

        // 1. 해당 '연도'의 월별 '목표' 맵 조회
        Map<Integer, BigDecimal> targetMap = salesTargetRepository.findByTargetYear(year).stream()
                .collect(Collectors.toMap(SalesTarget::getTargetMonth, SalesTarget::getTargetAmount));

        // 2. 해당 '연도'의 월별 '매출' 맵 조회
        Map<Integer, BigDecimal> salesMap = salesRepository.findMonthlySalesSummaryByYear(year).stream()
                .collect(Collectors.toMap(MonthlySalesSummary::getMonth, MonthlySalesSummary::getTotalSalesAmount));

        // 3. 1월부터 12월까지 순회하며 DTO 리스트 생성
        return IntStream.rangeClosed(1, 12).mapToObj(month -> {
            BigDecimal target = targetMap.getOrDefault(month, BigDecimal.ZERO);
            BigDecimal sales = salesMap.getOrDefault(month, BigDecimal.ZERO);

            BigDecimal ratio = BigDecimal.ZERO;
            if (target.compareTo(BigDecimal.ZERO) > 0) {
                ratio = sales.divide(target, 4, RoundingMode.HALF_UP);
            }

            return PeriodSalesStatusDTO.builder()
                    .month(month)
                    .targetAmount(target)
                    .salesAmount(sales)
                    .achievementRatio(ratio)
                    .build();
        }).collect(Collectors.toList());
    }

    /**
     * [신규] 연도별 매출 현황
     */
    @Override
    public List<YearlySalesStatusDTO> getYearlySalesStatus() {
        // 1. 연도별 목표액 맵
        Map<Integer, BigDecimal> targetMap = salesTargetRepository.findYearlyTargetSummary().stream()
                .collect(Collectors.toMap(
                        obj -> (Integer) obj[0], // year
                        obj -> (BigDecimal) obj[1]  // SUM(targetAmount)
                ));

        // 2. 연도별 매출액 맵
        Map<Integer, BigDecimal> salesMap = salesRepository.findYearlySalesSummary().stream()
                .collect(Collectors.toMap(YearlySalesSummary::getYear, YearlySalesSummary::getTotalSalesAmount));

        // 3. 모든 연도 키 취합 (중복 제거 및 정렬)
        Set<Integer> years = new HashSet<>(targetMap.keySet());
        years.addAll(salesMap.keySet());
        List<Integer> sortedYears = years.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());

        // 4. DTO로 조합
        return sortedYears.stream().map(year -> {
            BigDecimal target = targetMap.getOrDefault(year, BigDecimal.ZERO);
            BigDecimal sales = salesMap.getOrDefault(year, BigDecimal.ZERO);
            BigDecimal ratio = BigDecimal.ZERO;
            if (target.compareTo(BigDecimal.ZERO) > 0) {
                ratio = sales.divide(target, 4, RoundingMode.HALF_UP);
            }
            return YearlySalesStatusDTO.builder()
                    .year(year)
                    .targetAmount(target)
                    .salesAmount(sales)
                    .achievementRatio(ratio)
                    .build();
        }).collect(Collectors.toList());
    }


    // --- 🔽 CollectionService에서 완전히 이동해 온 메서드들 🔽 ---

    /**
     * (private 헬퍼로 변경) 미수금 계산 로직
     */
    private List<ClientReceivablesDTO> getClientReceivablesSummaryInternal() {
        List<ClientSalesSummary> salesSummaries = salesRepository.findTotalSalesAmountGroupByClient();
        List<CollectionSummary> collectionSummaries = collectionRepository.findTotalCollectionAmountGroupByClient();

        Map<String, ClientReceivablesDTO> receivablesMap = new HashMap<>();

        for (ClientSalesSummary sale : salesSummaries) {
            receivablesMap.put(
                    sale.getClientId(),
                    ClientReceivablesDTO.builder()
                            .clientId(sale.getClientId())
                            .clientCompany(sale.getClientCompany())
                            .totalSalesAmount(sale.getTotalSalesAmount())
                            .totalCollectionAmount(BigDecimal.ZERO)
                            .build()
            );
        }

        for (CollectionSummary col : collectionSummaries) {
            receivablesMap.compute(col.getClientId(), (k, v) -> {
                if (v == null) {
                    return ClientReceivablesDTO.builder()
                            .clientId(col.getClientId())
                            .clientCompany(col.getClientCompany())
                            .totalSalesAmount(BigDecimal.ZERO)
                            .totalCollectionAmount(col.getTotalCollectionAmount())
                            .build();
                }
                v.setTotalCollectionAmount(col.getTotalCollectionAmount());
                return v;
            });
        }

        return receivablesMap.values().stream()
                .peek(dto -> dto.setOutstandingBalance(
                        dto.getTotalSalesAmount().subtract(dto.getTotalCollectionAmount())
                ))
                .sorted(Comparator.comparing(ClientReceivablesDTO::getClientCompany))
                .collect(Collectors.toList());
    }

    @Override
    public List<ClientReceivablesDTO> getClientReceivablesSummary() {
        return this.getClientReceivablesSummaryInternal();
    }

    @Override
    public List<CollectionSummary> getClientTotalCollectionSummary() {
        return collectionRepository.findTotalCollectionAmountGroupByClient();
    }

    @Override
    public List<ClientSalesSummary> getClientTotalSalesSummary() {
        return salesRepository.findTotalSalesAmountGroupByClient();
    }

    @Override
    public List<ProjectSalesSummary> getProjectTotalSalesSummary() {
        return salesRepository.findTotalSalesAmountGroupByProject();
    }

    @Override
    public List<QuarterlySalesSummary> getQuarterlyTotalSalesSummary() {
        return salesRepository.findTotalSalesAmountGroupByQuarter();
    }
}
