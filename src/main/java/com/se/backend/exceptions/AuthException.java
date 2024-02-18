package com.se.backend.exceptions;

import lombok.Getter;

public class AuthException extends Exception {
    @Getter
    private final ErrorType type;
    private final String message;

    @Getter
    public enum ErrorType {
        PASSWORD_NOT_MATCH("Incorrect password"),
        USER_NOT_FOUND("User not found"),
        TOKEN_EXPIRED("Invalid token");

        private final String message;

        ErrorType(String message) {
            this.message = message;
        }

    }

    public AuthException(ErrorType type) {
        super(type.getMessage()); // 调用父类的构造器设置异常信息
        this.type = type;
        this.message = type.getMessage();
    }

    @Override
    public String getMessage() {
        return message;
    }
}