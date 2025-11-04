package com.bizmate.hr.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * [사용자 계정 수정 요청 DTO]
 * - 관리자 페이지에서 계정 활성화/잠금 상태 변경 및 비밀번호 재설정 시 사용
 */
@Getter
@NoArgsConstructor
public class UserUpdateRequestDTO {

    // 1. 계정 활성화/잠금 상태 변경 시 사용
    // True: 잠금 해제(N), False: 잠금(Y)
    private boolean isAccountNonLocked;

    // 2. 역할(Role) 변경 시 사용 (부여할 새로운 역할 ID 목록)
    // 기존 역할을 이 목록으로 대체합니다.
    private List<Long> roleIds;

    // 3. 비밀번호 재설정 시 사용 (Optional)
    // 서비스 단에서 이 필드가 null이 아닌 경우에만 비밀번호 해시 로직을 실행합니다.
    private String newPassword;
}
