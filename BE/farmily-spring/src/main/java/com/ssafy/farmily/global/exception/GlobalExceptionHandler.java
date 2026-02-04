package com.ssafy.farmily.global.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 전역 예외 처리 핸들러
 * 모든 컨트롤러에서 발생하는 예외를 처리
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Business Exception (Custom Exceptions)
     */
    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ErrorResponse> handleBusinessException(final BusinessException e,
            HttpServletRequest request) {
        log.warn("Business Exception: {} at path: {}", e.getMessage(), request.getRequestURI());
        final ErrorCode errorCode = e.getErrorCode();
        final ErrorResponse response = ErrorResponse.of(errorCode, request.getRequestURI());
        return new ResponseEntity<>(response, errorCode.getStatus());
    }

    /**
     * Access Denied Exception
     */
    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<ErrorResponse> handleAccessDeniedException(final AccessDeniedException e,
            HttpServletRequest request) {
        log.warn("Access Denied: {} at path: {}", e.getMessage(), request.getRequestURI());
        final ErrorCode errorCode = e.getErrorCode();
        final ErrorResponse response = ErrorResponse.of(errorCode, request.getRequestURI());
        return new ResponseEntity<>(response, errorCode.getStatus());
    }

    /**
     * Security Exception (Java Standard) -> Access Denied로 처리
     */
    @ExceptionHandler(SecurityException.class)
    protected ResponseEntity<ErrorResponse> handleSecurityException(SecurityException e, HttpServletRequest request) {
        log.warn("Security Exception: {} at path: {}", e.getMessage(), request.getRequestURI());
        final ErrorResponse response = new ErrorResponse(HttpStatus.FORBIDDEN, e.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    /**
     * Resource Not Found Exception
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex,
            HttpServletRequest request) {
        log.warn("Resource not found: {} at path: {}", ex.getMessage(), request.getRequestURI());
        final ErrorResponse response = new ErrorResponse(ErrorCode.ENTITY_NOT_FOUND.getStatus(), ex.getMessage(),
                request.getRequestURI());
        return new ResponseEntity<>(response, ErrorCode.ENTITY_NOT_FOUND.getStatus());
    }

    /**
     * Daily Limit Exceeded Exception
     */
    @ExceptionHandler(DailyLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleDailyLimitExceeded(DailyLimitExceededException ex,
            HttpServletRequest request) {
        log.warn("Daily limit exceeded: {} at path: {}", ex.getMessage(), request.getRequestURI());
        final ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(),
                request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex,
            HttpServletRequest request) {
        log.warn("Illegal argument: {} at path: {}", ex.getMessage(), request.getRequestURI());
        final ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, request.getRequestURI());
        return ResponseEntity.status(ErrorCode.INVALID_INPUT_VALUE.getStatus()).body(response);
    }

    /**
     * Generic Exception
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unhandled Exception: {} at path: {}", ex.getClass().getName(), request.getRequestURI(), ex);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR, request.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
