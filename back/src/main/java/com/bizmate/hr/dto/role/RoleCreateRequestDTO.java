package com.bizmate.hr.dto.role;

import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * [역할 등록 요청 DTO]
 * - 새로운 Role 생성 시 사용
 */
@Getter
@NoArgsConstructor
public class RoleCreateRequestDTO {

    @NotBlank(message = "역할명은 필수입니다.")
    @Size(max = 50, message = "역할명은 50자 이하로 입력해야 합니다.")
    private String roleName;    // 역할명 (예: ROLE_HR_ADMIN)

    @Size(max = 255, message = "설명은 255자 이하로 입력해야 합니다.")
    private String description; // 역할 설명

    private String isUsed;

    // 생성 시 부여할 권한 ID 목록
    private List<Long> permissionIds;
}