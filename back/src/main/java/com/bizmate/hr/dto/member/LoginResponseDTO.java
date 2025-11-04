package com.bizmate.hr.dto.member;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 이전에 포스트맨에서 확인했던 응답 구조와 일치하도록 구성
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private Long empId;
    private String empName;
    private Long userId;
    private String username;
    private String deptName;
    private String deptCode;
    private String email;

    private List<String> roles; // 역할 목록 (예: "CEO", "MANAGER")
    private List<String> perms; // 권한 목록 (예: "sys:admin", "emp:read")

    private String accessToken;
    private String refreshToken;
}