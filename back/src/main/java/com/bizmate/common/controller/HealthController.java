package com.bizmate.common.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ✅ 서버 상태(Health Check) 컨트롤러
 * React에서 주기적으로 서버 상태를 확인하기 위해 사용됩니다.
 * 누구나 접근 가능 (인증 불필요)
 */
@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<String> checkHealth() {
        return ResponseEntity.ok("OK");
    }
}
