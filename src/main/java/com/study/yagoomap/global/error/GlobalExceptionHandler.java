package com.study.yagoomap.global.error;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException exception, HttpServletRequest request) {
        ErrorCode errorCode = exception.errorCode();
        String message = exception.detail() == null || exception.detail().isBlank()
                ? errorCode.message()
                : exception.detail();
        return ResponseEntity
                .status(errorCode.status())
                .body(ErrorResponse.of(errorCode, message, request.getRequestURI()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, HttpServletRequest request) {
        List<ErrorResponse.FieldError> errors = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> new ErrorResponse.FieldError(error.getField(), error.getDefaultMessage(), error.getRejectedValue()))
                .toList();
        return ResponseEntity
                .status(ErrorCode.INVALID_REQUEST.status())
                .body(ErrorResponse.of(ErrorCode.INVALID_REQUEST, ErrorCode.INVALID_REQUEST.message(), request.getRequestURI(), errors));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(BindException exception, HttpServletRequest request) {
        List<ErrorResponse.FieldError> errors = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> new ErrorResponse.FieldError(error.getField(), error.getDefaultMessage(), error.getRejectedValue()))
                .toList();
        return ResponseEntity
                .status(ErrorCode.INVALID_REQUEST.status())
                .body(ErrorResponse.of(ErrorCode.INVALID_REQUEST, ErrorCode.INVALID_REQUEST.message(), request.getRequestURI(), errors));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException exception, HttpServletRequest request) {
        return ResponseEntity
                .status(ErrorCode.INVALID_JSON.status())
                .body(ErrorResponse.of(ErrorCode.INVALID_JSON, ErrorCode.INVALID_JSON.message(), request.getRequestURI()));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameter(MissingServletRequestParameterException exception, HttpServletRequest request) {
        String message = exception.getParameterName() + " 파라미터는 필수입니다.";
        return ResponseEntity
                .status(ErrorCode.MISSING_PARAMETER.status())
                .body(ErrorResponse.of(ErrorCode.MISSING_PARAMETER, message, request.getRequestURI()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException exception, HttpServletRequest request) {
        String message = exception.getName() + " 파라미터 타입이 올바르지 않습니다.";
        return ResponseEntity
                .status(ErrorCode.TYPE_MISMATCH.status())
                .body(ErrorResponse.of(ErrorCode.TYPE_MISMATCH, message, request.getRequestURI()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException exception, HttpServletRequest request) {
        List<ErrorResponse.FieldError> errors = exception.getConstraintViolations().stream()
                .map(violation -> new ErrorResponse.FieldError(
                        violation.getPropertyPath().toString(),
                        violation.getMessage(),
                        violation.getInvalidValue()
                ))
                .toList();
        return ResponseEntity
                .status(ErrorCode.INVALID_REQUEST.status())
                .body(ErrorResponse.of(ErrorCode.INVALID_REQUEST, ErrorCode.INVALID_REQUEST.message(), request.getRequestURI(), errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception, HttpServletRequest request) {
        log.error("Unhandled exception: {}", request.getRequestURI(), exception);
        return ResponseEntity
                .status(ErrorCode.INTERNAL_SERVER_ERROR.status())
                .body(ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_SERVER_ERROR.message(), request.getRequestURI()));
    }
}
