package com.bizmate.hr.dto.role;

import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * [역할 수정 요청 DTO]
 * - 기존 Role의 정보 및 부여된 권한 목록 수정 시 사용
 */
@Getter
@NoArgsConstructor
public class RoleUpdateRequestDTO {

    @Size(max = 50, message = "역할명은 50자 이하로 입력해야 합니다.")
    private String roleName;    // 새로운 역할명

    @Size(max = 255, message = "설명은 255자 이하로 입력해야 합니다.")
    private String description; // 새로운 설명

    private String isUsed;

    // 수정할 권한 ID 목록 (기존 목록을 대체하거나 추가/삭제)
    private List<Long> permissionIds;
}