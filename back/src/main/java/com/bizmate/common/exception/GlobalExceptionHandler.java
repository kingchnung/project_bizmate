package com.bizmate.common.exception;

import com.bizmate.common.exception.VerificationFailedException; // 다른 모듈의 예외 클래스
import com.bizmate.hr.advice.LoginFailedException;
import io.jsonwebtoken.JwtException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.BindException;
import java.util.Map;

/**
 * [GlobalExceptionHandler]
 * - 프로젝트 전체의 Controller 계층에서 발생하는 주요 예외를 처리하고
 * 표준화된 JSON 응답을 반환하는 통합 예외 처리기입니다.
 * - 여러 Advice 클래스로 인해 발생하던 우선순위 충돌 문제를 해결합니다.
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.bizmate") // 적용 범위를 전체 패키지로 설정
public class GlobalExceptionHandler {

    // ====================================================================
    // 1. 비즈니스 로직 및 유효성 검증 관련 예외 (클라이언트 오류: 4xx)
    // ====================================================================

    /**
     * [로그인 실패] 전용 핸들러 (HTTP 400 Bad Request)
     */
    @ExceptionHandler(LoginFailedException.class)
    public ResponseEntity<?> handleLoginFailed(LoginFailedException e) {
        log.warn("로그인 실패: {}", e.getMessage());
        return ResponseEntity.badRequest().body(Map.of(
                "code", "LOGIN_FAILED",
                "message", e.getMessage()
        ));
    }

    /**
     * [비즈니스 규칙 위반] (HTTP 400 Bad Request)
     */
    @ExceptionHandler(VerificationFailedException.class)
    public ResponseEntity<?> handleVerification(VerificationFailedException e) {
        log.warn("비즈니스 규칙 위반: {}", e.getMessage());
        return ResponseEntity.badRequest().body(Map.of(
                "code", "BUSINESS_VALIDATION",
                "message", e.getMessage()
        ));
    }

    /**
     * [DTO 유효성 검증 실패] @Valid, @Validated (HTTP 400 Bad Request)
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<?> handleValidation(Exception e) {
        log.warn("입력값 유효성 검증 실패: {}", e.getMessage());
        // 간단 응답 (필요 시 필드별 메시지 수집 로직 추가 가능)
        return ResponseEntity.badRequest().body(Map.of(
                "code", "VALIDATION_ERROR",
                "message", "입력값을 확인해 주세요."
        ));
    }

    /**
     * [데이터 없음] (HTTP 404 Not Found)
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleEntityNotFound(EntityNotFoundException e) {
        log.warn("데이터 조회 실패: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "code", "NOT_FOUND",
                "message", e.getMessage()
        ));
    }

    /**
     * [JWT 관련 오류] (HTTP 403 Forbidden)
     */
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<?> handleJwt(JwtException e) {
        log.error("JWT 오류 발생: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                "code", "JWT_ERROR",
                "message", "유효하지 않은 토큰이거나 인증에 실패했습니다."
        ));
    }

    /**
     * [데이터베이스 충돌] 동시성 제어 (HTTP 409 Conflict)
     */
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<?> handleLocking(ObjectOptimisticLockingFailureException e) {
        log.warn("데이터베이스 충돌 발생: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "code", "CONFLICT",
                "message", "다른 사용자가 동시에 수정했습니다. 다시 시도해주세요."
        ));
    }

    /**
     * [데이터베이스 무결성 제약 위반] (HTTP 400 Bad Request)
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrity(DataIntegrityViolationException e) {
        log.error("데이터 무결성 제약 위반: {}", e.getMessage());
        return ResponseEntity.badRequest().body(Map.of(
                "code", "DATA_INTEGRITY",
                "message", "데이터베이스 제약 조건(예: 중복된 값)을 위반했습니다."
        ));
    }


    // ====================================================================
    // 2. 예상치 못한 서버 오류 (서버 오류: 5xx)
    // ====================================================================

    /**
     * [서버 내부 로직 오류] 예상치 못한 RuntimeException 처리
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntime(RuntimeException e) {
        log.error("예상치 못한 런타임 예외 발생", e);
        return ResponseEntity.internalServerError().body(Map.of(
                "code", "UNEXPECTED_RUNTIME_ERROR",
                "message", "서버 처리 중 예기치 않은 오류가 발생했습니다."
        ));
    }

    /**
     * [최종 안전망] 처리되지 않은 모든 예외
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAll(Exception e) {
        log.error("처리되지 않은 최상위 예외 발생", e);
        return ResponseEntity.internalServerError().body(Map.of(
                "code", "UNHANDLED_ERROR",
                "message", "서버 내부에서 알 수 없는 오류가 발생했습니다."
        ));
    }

    /**
     * [권한 거부 / 접근 제한] (HTTP 403 Forbidden)
     * - SecurityException, AccessDeniedException 등 권한 관련 예외를 처리합니다.
     * - 공지사항 수정/삭제, 관리자 전용 기능 접근 시 권한이 없을 경우 발생합니다.
     * - 클라이언트에는 "FORBIDDEN" 코드와 함께 사용자 친화적인 메시지를 반환합니다.
     */
    @ExceptionHandler(ForbiddenOperationException.class)
    public ResponseEntity<?> handleForbiddenOperation(ForbiddenOperationException e) {
        log.warn("권한 거부(도메인): {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                "code", "FORBIDDEN",
                "message", e.getMessage() != null ? e.getMessage() : "권한이 없습니다."
        ));
    }
}
