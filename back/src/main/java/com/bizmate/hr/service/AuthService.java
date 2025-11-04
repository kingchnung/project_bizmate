package com.bizmate.hr.service;

import com.bizmate.hr.dto.member.LoginRequestDTO;
import com.bizmate.hr.dto.member.ResetPasswordRequest;

import java.util.Map;

public interface AuthService {
    /**
     * 🔹 로그인 수행 (JWT AccessToken / RefreshToken 발급)
     * @param request 로그인 요청 DTO (username, password)
     * @return accessToken, refreshToken, userId, roles, empId 등을 포함한 Map
     */
    Map<String, Object> login(LoginRequestDTO request);

    /**
     * 🔹 Refresh Token을 통해 Access Token 재발급
     * @param refreshToken 클라이언트가 보유한 refreshToken
     * @return 새 accessToken 및 refreshToken
     */
    Map<String, Object> refresh(String refreshToken);

    /**
     * 🔹 비밀번호 찾기 (비로그인 상태)
     * - username + email 검증 후 임시 비밀번호를 메일로 발송
     * @param dto ResetPasswordRequest DTO
     */
    void resetPassword(ResetPasswordRequest dto);
}
