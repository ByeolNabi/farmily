package com.ssafy.farmily.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
public class ErrorResponse {

    private final LocalDateTime timestamp = LocalDateTime.now();
    private final int status;
    private final String error;
    private final String code;
    private final String message;
    private final String path;

    public ErrorResponse(ErrorCode errorCode, String path) {
        this.status = errorCode.getStatus().value();
        this.error = errorCode.getStatus().name();
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.path = path;
    }

    public ErrorResponse(HttpStatus status, String message, String path) {
        this.status = status.value();
        this.error = status.name();
        this.code = "";
        this.message = message;
        this.path = path;
    }

    public ErrorResponse(int status, String error, String message, String path) {
        this.status = status;
        this.error = error;
        this.code = "";
        this.message = message;
        this.path = path;
    }

    public static ErrorResponse of(ErrorCode errorCode, String path) {
        return new ErrorResponse(errorCode, path);
    }
}
