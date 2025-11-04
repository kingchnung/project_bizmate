package com.bizmate.salesPages.management.collections.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CollectionDTO {
    private Long collectionNo;
    private String collectionId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate collectionDate;

    private BigDecimal collectionMoney;
    private String collectionNote;
    private String writer;
    private String userId;
    private String clientId;
    private String clientCompany;
}
