package com.AI.chatbot.exception;

public class AppException extends RuntimeException {
    private ErrorType errorType;

    public AppException(ErrorType errorType, String message) {
        super(message);
        this.errorType = errorType;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public enum ErrorType {
        aws_credentials_fail,
        // 다른 에러 타입을 여기에 정의할 수 있습니다.
    }
}
