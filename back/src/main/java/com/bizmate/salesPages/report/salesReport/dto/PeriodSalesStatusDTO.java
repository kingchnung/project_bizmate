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
public class PeriodSalesStatusDTO {
    private Integer month;

    @Builder.Default
    private BigDecimal targetAmount = BigDecimal.ZERO; // 월 목표

    @Builder.Default
    private BigDecimal salesAmount = BigDecimal.ZERO; // 월 매출

    @Builder.Default
    private BigDecimal achievementRatio = BigDecimal.ZERO; // 달성률
}