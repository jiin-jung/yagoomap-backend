package com.study.yagoomap.global.error;

public class ApiException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String detail;

    public ApiException(ErrorCode errorCode) {
        this(errorCode, null);
    }

    public ApiException(ErrorCode errorCode, String detail) {
        super(detail == null || detail.isBlank() ? errorCode.message() : detail);
        this.errorCode = errorCode;
        this.detail = detail;
    }

    public ErrorCode errorCode() {
        return errorCode;
    }

    public String detail() {
        return detail;
    }
}
