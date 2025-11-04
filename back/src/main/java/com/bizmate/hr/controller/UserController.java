package com.bizmate.hr.controller;



import com.bizmate.hr.dto.user.UserDTO;
import com.bizmate.hr.dto.user.UserPwChangeRequest;
import com.bizmate.hr.dto.user.UserUpdateRequestDTO;
import com.bizmate.hr.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // 1. 전체 사용자 목록 조회 (관리자용)
    @GetMapping
    @PreAuthorize("hasAuthority('sys:admin')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // 2. 특정 사용자 정보 조회 (관리자용)
    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('sys:admin')")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long userId) {

        UserDTO userDTO = userService.getUser(userId);

        return ResponseEntity.ok(userDTO);
    }

    // 3. 내 정보 조회 (인증된 모든 사용자용)
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> getMyInfo() {
        UserDTO currentUser = (UserDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(currentUser);
    }

    // 4. 특정 사용자 정보 수정 (Update)
    @PutMapping("/{userId}")
    @PreAuthorize("hasAuthority('sys:admin')")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long userId, @RequestBody UserUpdateRequestDTO updateDTO) {
        UserDTO updatedUser = userService.updateUser(userId, updateDTO);
        return ResponseEntity.ok(updatedUser);
    }

    // 로그인한 사용자의 비밀번호 변경
    @PutMapping("/{id}/password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> changePassword(
            @PathVariable Long id, @RequestBody
            UserPwChangeRequest dto) {
        userService.changePw(id, dto);
        return ResponseEntity.ok(Map.of("message", "비밀번호가 변경되었습니다."));
    }

    //비밀번호 초기화
    @PutMapping("/{userId}/reset-lock")
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_CEO')")
    public ResponseEntity<Map<String, Object>> resetUserLock(@PathVariable Long userId) {
        userService.adminResetPassword(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "계정이 정상적으로 초기화되었습니다.");
        response.put("userId", userId);
        return ResponseEntity.ok(response);
    }

    //계정활성화 비활성화
    @PutMapping("/{userId}/active")
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_CEO')")
    public ResponseEntity<Map<String, Object>> setUserActiveStatus(
            @PathVariable Long userId,
            @RequestBody Map<String, Object> body) {

        // 프론트에서 active: true/false 로 보내더라도 'Y'/'N'으로 변환
        Object val = body.get("active");
        String activeStatus = "Y";

        if (val instanceof Boolean) {
            activeStatus = ((Boolean) val) ? "Y" : "N";
        } else if (val instanceof String) {
            activeStatus = ((String) val).equalsIgnoreCase("Y") ? "Y" : "N";
        }

        userService.setUserActiveStatus(userId, activeStatus);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "계정이 " + ("Y".equals(activeStatus) ? "활성화" : "비활성화") + "되었습니다.");
        response.put("userId", userId);
        response.put("isActive", activeStatus);

        return ResponseEntity.ok(response);
    }



    // 5. 특정 사용자 계정 삭제 (Delete)
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAuthority('sys:admin')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
