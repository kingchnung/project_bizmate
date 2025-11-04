package com.bizmate.salesPages.report.salesTarget.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SalesTargetDTO {
    private Long targetId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate registrationDate;

    private Integer targetYear;
    private Integer targetMonth;
    private BigDecimal targetAmount;
    private String userId;
    private String writer;
}
