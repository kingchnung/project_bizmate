package com.bizmate.hr.dto.code;

import com.bizmate.hr.domain.code.Grade;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GradeDTO {
    private Long gradeCode;
    private String gradeName;

    private int gradeOrder;
    private String isUsed;

    public static GradeDTO fromEntity(Grade grade) {
        if (grade == null) return null;

        return GradeDTO.builder()
                .gradeCode(grade.getGradeCode())
                .gradeName(grade.getGradeName())

                .gradeOrder(grade.getGradeOrder())
                .isUsed(grade.getIsUsed())
                .build();
    }
}
