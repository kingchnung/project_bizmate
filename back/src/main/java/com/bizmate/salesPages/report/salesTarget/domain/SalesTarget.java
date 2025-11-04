package com.bizmate.salesPages.report.salesTarget.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(
            name = "UK_SALES_TARGET_YEAR_MONTH", // 제약조건 이름
            columnNames = {"targetYear", "targetMonth"} // 이 두 컬럼의 조합이 고유해야 함
        )}
)
@SequenceGenerator(
        name = "SALES_TARGET_SEQ_GENERATOR",
        sequenceName = "SALES_TARGET_SEQ",
        initialValue = 1,
        allocationSize = 1
)
@Audited
public class SalesTarget {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "SALES_TARGET_SEQ_GENERATOR"
    )
    private Long targetId;

    @CreationTimestamp
    @Temporal(TemporalType.DATE)
    private LocalDate registrationDate;

    private Integer targetYear;
    private Integer targetMonth;
    private BigDecimal targetAmount;
    private String userId;
    private String writer;

    public void changTargetYear(Integer targetYear){
        this.targetYear = targetYear;
    }

    public void changeTargetMonth(Integer targetMonth){
        this.targetMonth = targetMonth;
    }

    public void changeTargetAmount(BigDecimal targetAmount){
        this.targetAmount = targetAmount;
    }
}
