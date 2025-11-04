package com.bizmate.hr.advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 로그인 실패 시 발생하는 전용 예외 클래스입니다.
 * RuntimeException을 상속받아 Unchecked Exception으로 처리됩니다.
 * * @ResponseStatus(HttpStatus.BAD_REQUEST)
 * 이 어노테이션은 GlobalExceptionHandler가 이 예외를 잡지 못했을 경우를 대비한
 * 2차 방어선으로, 스프링이 이 예외를 보면 기본적으로 400 (Bad Request) 상태 코드를
 * 응답하도록 지정합니다.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class LoginFailedException extends RuntimeException {

    /**
     * 예외 발생 시 구체적인 메시지(예: "비밀번호가...")를
     * 부모 클래스(RuntimeException)로 전달하는 생성자입니다.
     * * @param message "비밀번호가 일치하지 않습니다.", "사용자를 찾을 수 없습니다." 등의 오류 메시지
     */
    public LoginFailedException(String message) {
        // super(message)는 RuntimeException(String message) 생성자를 호출합니다.
        // 이로 인해 나중에 ex.getMessage()로 "비밀번호가..." 메시지를 꺼낼 수 있습니다.
        super(message);
    }

    /**
     * (선택 사항) 근본 원인(cause) 예외를 함께 전달할 경우 사용하는 생성자입니다.
     * * @param message 오류 메시지
     * @param cause 근본 원인이 된 예외 (예: DB 연결 오류 등)
     */
    public LoginFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}