package com.bizmate.salesPages.report.salesReport.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollectionSummary {
    private String clientId;
    private String clientCompany;
    private BigDecimal totalCollectionAmount;
}
