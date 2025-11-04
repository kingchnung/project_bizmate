package com.bizmate.salesPages.management.sales.sales.dto;

import com.bizmate.salesPages.management.sales.salesItem.dto.SalesItemDTO;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SalesDTO {
    private Long salesNo;

    private String salesId;
    private LocalDate salesIdDate;
    private LocalDate salesDate;

    private String projectId;
    private String projectName;
    private LocalDate deploymentDate;
    private BigDecimal salesAmount;
    private BigDecimal totalSubAmount;
    private BigDecimal totalVatAmount;
    private String userId;
    private String writer;
    private String clientId;
    private String clientCompany;
    private String salesNote;
    private boolean invoiceIssued;

    // 연관된 Order 정보는 ID만 포함 (Optional)
    private String orderId;

    private List<SalesItemDTO> salesItems;

}
