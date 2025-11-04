package com.bizmate.salesPages.report.salesReport.controller;



import com.bizmate.common.page.PageRequestDTO;
import com.bizmate.common.page.PageResponseDTO;
import com.bizmate.salesPages.report.salesReport.dto.*;
import com.bizmate.salesPages.report.salesReport.service.SalesReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sales/report") // 👈 리포트 전용 상위 경로
@RequiredArgsConstructor
public class SalesReportController {

    private final SalesReportService salesReportService;

    /**
     * [신규] 매출 현황 - 거래처별 (페이징, 연/월 필터)
     * GET /api/sales/report/status/client?year=2025&month=10&page=1&size=10
     */
    @GetMapping("/status/client")
    public PageResponseDTO<ClientSalesStatusDTO> getClientSalesStatus(
            PageRequestDTO pageRequestDTO,
            @RequestParam("year") Integer year,
            @RequestParam(value = "month", required = false) Integer month
    ) {
        return salesReportService.getClientSalesStatus(pageRequestDTO, year, month);
    }

    /**
     * [신규] 매출 현황 - 기간별 (연 필터)
     * GET /api/sales/report/status/period?year=2025
     */
    @GetMapping("/status/period")
    public ResponseEntity<List<PeriodSalesStatusDTO>> getPeriodSalesStatus(
            @RequestParam("year") Integer year
    ) {
        return ResponseEntity.ok(salesReportService.getPeriodSalesStatus(year));
    }


    // --- 🔽 CollectionController에서 완전히 이동해 온 엔드포인트들 🔽 ---

    /**
     * 거래처별 매출/수금/미수금 요약 (발행완료 매출 기준)
     * 프론트: GET /api/sales/report/receivables (경로 변경됨!)
     */
    @GetMapping("/receivables")
    public ResponseEntity<List<ClientReceivablesDTO>> getReceivablesIssued() {
        return ResponseEntity.ok(salesReportService.getClientReceivablesSummary());
    }

    @GetMapping("/receivables/client")
    public List<ClientReceivablesDTO> getClientReceivablesSummary() {
        return salesReportService.getClientReceivablesSummary();
    }

    @GetMapping("/collection/client")
    public List<CollectionSummary> getClientCollectionSummary() {
        return salesReportService.getClientTotalCollectionSummary();
    }

    @GetMapping("/sales/client")
    public List<ClientSalesSummary> getClientSalesSummary() {
        return salesReportService.getClientTotalSalesSummary();
    }

    @GetMapping("/sales/project")
    public List<ProjectSalesSummary> getProjectSalesSummary() {
        return salesReportService.getProjectTotalSalesSummary();
    }

    @GetMapping("/sales/quarter")
    public List<QuarterlySalesSummary> getQuarterlySalesSummary() {
        return salesReportService.getQuarterlyTotalSalesSummary();
    }

    /**
     * [신규] 매출 현황 - 연도별 요약 (기간별 현황 탭용)
     * GET /api/sales/report/status/annual
     */
    @GetMapping("/status/annual")
    public ResponseEntity<List<YearlySalesStatusDTO>> getYearlySalesStatus() {
        return ResponseEntity.ok(salesReportService.getYearlySalesStatus());
    }
}