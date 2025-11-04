package com.bizmate.hr.dto.code;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * [직책 등록 요청 DTO]
 */
@Getter
@NoArgsConstructor
public class PositionCreateRequestDTO {

    @NotBlank(message = "직책명은 필수입니다.")
    @Size(max = 100, message = "직책명은 100자 이하로 입력해야 합니다.")
    private String positionName;

    @Size(max = 500, message = "설명은 500자 이하로 입력해야 합니다.")
    private String description;

    // isUsed 필드는 등록 시 기본값 'Y'로 서비스 계층에서 자동 설정됩니다.
}