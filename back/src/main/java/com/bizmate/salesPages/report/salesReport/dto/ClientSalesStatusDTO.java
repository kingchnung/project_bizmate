package com.bizmate.salesPages.report.salesReport.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientSalesStatusDTO {
    private String clientId;
    private String clientCompany;

    @Builder.Default
    private BigDecimal monthlySalesAmount = BigDecimal.ZERO; // 해당 월 매출액

    @Builder.Default
    private BigDecimal achievementRatio = BigDecimal.ZERO; // 월 목표 대비 매출 비율

    @Builder.Default
    private BigDecimal outstandingBalance = BigDecimal.ZERO; // (전체 기간) 미수금액
}