package com.bizmate.hr.dto.code;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PositionRequestDTO {
    @NotBlank(message = "직책 이름은 필수 항목입니다.")
    private String positionName;

    private String description; // 직무기술은 선택 사항

    private String isUsed = "Y";
}
