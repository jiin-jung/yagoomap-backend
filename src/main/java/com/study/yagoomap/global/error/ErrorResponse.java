package com.study.yagoomap.global.error;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String code,
        String message,
        String path,
        List<FieldError> errors
) {

    public static ErrorResponse of(ErrorCode errorCode, String message, String path) {
        return new ErrorResponse(
                LocalDateTime.now(),
                errorCode.status().value(),
                errorCode.code(),
                message,
                path,
                List.of()
        );
    }

    public static ErrorResponse of(ErrorCode errorCode, String message, String path, List<FieldError> errors) {
        return new ErrorResponse(
                LocalDateTime.now(),
                errorCode.status().value(),
                errorCode.code(),
                message,
                path,
                errors
        );
    }

    public record FieldError(String field, String message, Object rejectedValue) {
    }
}
