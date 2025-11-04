package com.bizmate.hr.dto.code;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * [직책 수정 요청 DTO]
 */
@Getter
@NoArgsConstructor
public class PositionUpdateRequestDTO {

    @Size(max = 100, message = "직책명은 100자 이하로 입력해야 합니다.")
    private String positionName;

    @Size(max = 500, message = "설명은 500자 이하로 입력해야 합니다.")
    private String description;

    private String isUsed; // 직책 활성화/비활성화 ('Y'/'N') 수정 가능
}