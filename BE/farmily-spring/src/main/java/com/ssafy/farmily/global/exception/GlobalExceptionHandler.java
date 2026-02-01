package com.ssafy.farmily.global.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 전역 예외 처리 핸들러
 * 모든 컨트롤러에서 발생하는 예외를 처리
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private Map<String, Object> createErrorBase(HttpStatus status, String error, String message, String path) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("status", status.value());
        response.put("error", error);
        response.put("message", message);
        response.put("path", path);
        return response;
    }

    /**
     * 리소스를 찾을 수 없을 때 (404)
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        log.warn("Resource not found: {} at path: {}", ex.getMessage(), request.getRequestURI());
        Map<String, Object> response = createErrorBase(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * 일일 제한 초과 등 잘못된 요청 (400)
     */
    @ExceptionHandler(DailyLimitExceededException.class)
    public ResponseEntity<Map<String, Object>> handleDailyLimitExceeded(DailyLimitExceededException ex, HttpServletRequest request) {
        log.warn("Daily limit exceeded: {} at path: {}", ex.getMessage(), request.getRequestURI());
        Map<String, Object> response = createErrorBase(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * IllegalArgumentException (400)
     * 잘못된 파라미터 등
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        log.warn("Illegal argument: {} at path: {}", ex.getMessage(), request.getRequestURI());
        Map<String, Object> response = createErrorBase(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * 그 외 모든 예외 (500)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unhandled Exception: {} at path: {}", ex.getClass().getName(), request.getRequestURI(), ex);
        
        Map<String, Object> response = createErrorBase(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", 
                "서버 내부 오류가 발생했습니다.", request.getRequestURI());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
