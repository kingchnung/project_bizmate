package com.bizmate.salesPages.report.salesReport.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ClientReceivablesDTO {
    private String clientId;
    private String clientCompany;
    private BigDecimal totalSalesAmount;
    private BigDecimal totalCollectionAmount;
    private BigDecimal outstandingBalance;
}
