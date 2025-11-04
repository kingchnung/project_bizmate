package com.bizmate.hr.dto.permission;

import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Size;

/**
 * [권한 수정 요청 DTO]
 * - 기존 Permission의 정보 수정 시 사용
 */
@Getter
@NoArgsConstructor
public class PermissionUpdateRequestDTO {

    // 권한명은 보통 불변(Immutable)으로 관리하지만, 필요 시 수정을 허용할 수 있습니다.
    @Size(max = 100, message = "권한명은 100자 이하로 입력해야 합니다.")
    private String permName;

    @Size(max = 255, message = "설명은 255자 이하로 입력해야 합니다.")
    private String description; // 새로운 설명

    private String isUsed;
}