package com.ssafy.farmily.global.exception;

/**
 * 일일 제한을 초과했을 때 발생하는 예외
 * HTTP 400 Bad Request로 처리됨
 */
public class DailyLimitExceededException extends RuntimeException {
    public DailyLimitExceededException(String message) {
        super(message);
    }
}
