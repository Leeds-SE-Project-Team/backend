package com.se.backend.exceptions;

import lombok.Getter;

public class AuthException extends Exception {
    @Getter
    private final ErrorType type;
    private final String message;

    @Getter
    public enum ErrorType {
        PASSWORD_NOT_MATCH("密码错误"),
        USER_NOT_FOUND("用户不存在"),
        TOKEN_EXPIRED("Token过期：其他设备登录，时间过长");

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