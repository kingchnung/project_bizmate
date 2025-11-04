package com.bizmate.salesPages.report.salesReport.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuarterlySalesSummary {
    private Integer year;
    private Integer quarter;
    private BigDecimal totalSalesAmount;
}
