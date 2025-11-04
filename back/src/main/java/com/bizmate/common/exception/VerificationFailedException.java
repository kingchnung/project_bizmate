package com.bizmate.common.exception;

public class VerificationFailedException extends RuntimeException{

    public VerificationFailedException(String message) {
        super(message);
    }

    public VerificationFailedException(String message, Throwable cause) {
        super(message, cause);
    }

}
