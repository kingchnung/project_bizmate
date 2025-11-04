package com.bizmate.salesPages.management.sales.salesItem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesItemDTO {
    private Long salesItemId;

    private String itemName;
    private Long quantity;
    private BigDecimal unitPrice;
    private BigDecimal unitVat;
    private BigDecimal totalAmount;
    private String itemNote;
    private Integer lineNum;
}
