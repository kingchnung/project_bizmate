package com.bizmate.hr.dto.code; // code 엔티티 패키지 경로를 따릅니다.

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * [직급 등록 요청 DTO]
 */
@Getter
@NoArgsConstructor
public class GradeCreateRequestDTO {

    @NotBlank(message = "직급명은 필수입니다.")
    @Size(max = 50, message = "직급명은 50자 이하로 입력해야 합니다.")
    private String gradeName;

    @NotNull(message = "직급 순서는 필수입니다.")
    private Integer gradeOrder; // 위계를 위한 숫자

    // isUsed 필드는 등록 시 기본값 'Y'로 서비스 계층에서 자동 설정됩니다.
}