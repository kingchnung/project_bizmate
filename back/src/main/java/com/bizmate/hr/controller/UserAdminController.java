package com.bizmate.hr.controller;


import com.bizmate.hr.dto.member.ResetPasswordRequest;
import com.bizmate.hr.dto.user.UserDTO;
import com.bizmate.hr.dto.user.UserUpdateRequestDTO;
import com.bizmate.hr.service.AuthService;
import com.bizmate.hr.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/users")
@PreAuthorize("hasAnyRole('ROLE_CEO', 'ROLE_ADMIN', 'ROLE_MANAGER')")
public class UserAdminController {
    private final UserService userService;


    // ✅ 1. 전체 사용자 목록 조회
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // ✅ 2. 특정 사용자 조회
    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    // ✅ 3. 사용자 정보 수정 (역할/비밀번호 등)
    @PutMapping("/{userId}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long userId, @RequestBody UserUpdateRequestDTO dto) {
        log.info("🛠️ [ADMIN] 사용자 수정 요청: userId={}", userId);
        return ResponseEntity.ok(userService.updateUser(userId, dto));
    }

    // ✅ 4. 계정 잠금 해제
    @PostMapping("/{userId}/unlock")
    public ResponseEntity<?> adminUnlockAccount(@PathVariable Long userId) {
        log.info("🔓 [ADMIN] 계정 잠금 해제 요청: userId={}", userId);
        userService.adminUnlockAccount(userId);
        return ResponseEntity.ok(Map.of("message", "계정의 잠금이 해제되었습니다."));
    }

    // ✅ 5. 관리자용 비밀번호 초기화 (임시 비밀번호 이메일 발송)
    @PutMapping("/{userId}/reset-password")
    public ResponseEntity<?> adminResetPassword(@PathVariable Long userId) {
        log.info("🌀 [ADMIN] 계정 초기화 요청: userId={}", userId);
        String tempPw = userService.adminResetPassword(userId);
        return ResponseEntity.ok(Map.of("message", "임시 비밀번호가 이메일로 발송되었습니다.", "tempPw", tempPw));
    }

    // ✅ 6. 계정 활성/비활성 설정
    @PutMapping("/{userId}/active")
    public ResponseEntity<?> setActive(@PathVariable Long userId, @RequestBody Map<String, Object> body) {
        Object val = body.get("active");
        String activeStatus = "Y";

        if (val instanceof Boolean) activeStatus = ((Boolean) val) ? "Y" : "N";
        if (val instanceof String) activeStatus = ((String) val).equalsIgnoreCase("Y") ? "Y" : "N";

        userService.setUserActiveStatus(userId, activeStatus);

        return ResponseEntity.ok(Map.of(
                "message", "계정이 " + ("Y".equals(activeStatus) ? "활성화" : "비활성화") + "되었습니다.",
                "userId", userId,
                "isActive", activeStatus
        ));
    }

    // ✅ 7. 계정 삭제
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        log.info("🗑️ [ADMIN] 계정 삭제 요청: userId={}", userId);
        userService.deleteUser(userId);
        return ResponseEntity.ok(Map.of("message", "계정이 삭제되었습니다."));
    }
}