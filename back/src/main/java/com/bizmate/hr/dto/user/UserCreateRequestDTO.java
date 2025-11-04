package com.bizmate.hr.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * [사용자 계정 등록 요청 DTO]
 * - 관리자 페이지에서 신규 사용자 계정 생성 시 사용
 */
@Getter
@NoArgsConstructor
public class UserCreateRequestDTO {

    @NotNull(message = "직원 ID는 필수입니다.")
    private Long empId;         // 계정을 연결할 직원 ID (FK)

    @NotBlank(message = "로그인 ID는 필수입니다.")
    @Size(min = 4, max = 100, message = "로그인 ID는 4자 이상 100자 이하로 입력해야 합니다.")
    private String username;    // 로그인 ID (사용자명)

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 4, message = "비밀번호는 최소 4자 이상이어야 합니다.")
    private String password;    // 평문 비밀번호 (서비스에서 pwHash로 변환됨)

    // 계정 생성 시 기본 ROLE을 부여하기 위한 정보
    @NotNull(message = "부여할 역할(Role) ID는 필수입니다.")
    private Long roleId;        // 부여할 역할 ID (예: 1=ROLE_USER, 2=ROLE_ADMIN)
}