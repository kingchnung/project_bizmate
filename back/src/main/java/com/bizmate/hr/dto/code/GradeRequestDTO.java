package com.bizmate.hr.dto.code;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GradeRequestDTO {
    @NotBlank(message = "직급 이름은 필수 항목입니다.")
    private String gradeName;

    private String description;

    @NotNull(message = "직급 순서는 필수 항목입니다.")
    @Min(value = 1, message = "직급 순서는 1 이상이어야 합니다.")
    private Integer gradeOrder; // ★ 필드명 변경

    private String isUsed = "Y";
}
