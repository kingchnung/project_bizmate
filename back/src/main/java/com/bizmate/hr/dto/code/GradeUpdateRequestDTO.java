package com.bizmate.hr.dto.code;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * [직급 수정 요청 DTO]
 */
@Getter
@NoArgsConstructor
public class GradeUpdateRequestDTO {

    @Size(max = 50, message = "직급명은 50자 이하로 입력해야 합니다.")
    private String gradeName;

    private Integer gradeOrder;

    private String isUsed; // 직급 활성화/비활성화 ('Y'/'N') 수정 가능
}