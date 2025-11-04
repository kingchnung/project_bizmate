package com.bizmate.hr.dto.permission;

import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * [권한 등록 요청 DTO]
 * - 새로운 Permission 생성 시 사용
 */
@Getter
@NoArgsConstructor
public class PermissionCreateRequestDTO {

    @NotBlank(message = "권한명은 필수입니다.")
    @Size(max = 100, message = "권한명은 100자 이하로 입력해야 합니다.")
    private String permName;    // 권한명 (예: hr_card:read, user:manage)

    @Size(max = 255, message = "설명은 255자 이하로 입력해야 합니다.")
    private String description; // 권한 설명

    private String isUsed = "Y";
}