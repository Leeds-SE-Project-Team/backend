package com.se.backend.exceptions;

import lombok.Getter;

public class ResourceException extends Exception {
    @Getter
    private final ErrorType type;
    private final String message;

    public ResourceException(ErrorType type) {
        super(type.getMessage()); // 调用父类的构造器设置异常信息
        this.type = type;
        this.message = type.getMessage();
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Getter
    public enum ErrorType {
        TOUR_NOT_FOUND("Tour not found"), TOUR_COLLECTION_NOT_FOUND("Tour collection not found"),


        TOUR_SPOT_NOT_FOUND("Tour spot not found"), TOUR_Highlight_NOT_FOUND("Tour highlight not found"),

        USER_NOT_FOUND("User not found"),


        GROUP_NOT_FOUND("Group not found"), GROUP_COLLECTION_NOT_FOUND("Group collection not found"),

        TOUR_LIKE_NOT_FOUND("Tour like not found"), TOUR_STAR_NOT_FOUND("Tour star not found"),
        TOUR_LIKE_EXISTS("tour has been liked"),TOUR_STAR_EXISTS("tour has been starred"),

        COMMENT_NOT_FOUND("Comment not found"), COMMENT_LIKE_NOT_FOUND("Comment like not found");
        private final String message;

        ErrorType(String message) {
            this.message = message;
        }

    }
}