package com.study.yagoomap.global.error;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_400_001", "요청 값이 올바르지 않습니다."),
    INVALID_JSON(HttpStatus.BAD_REQUEST, "COMMON_400_002", "요청 본문 형식이 올바르지 않습니다."),
    MISSING_PARAMETER(HttpStatus.BAD_REQUEST, "COMMON_400_003", "필수 요청 파라미터가 누락되었습니다."),
    TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "COMMON_400_004", "요청 파라미터 타입이 올바르지 않습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_500_001", "서버 내부 오류가 발생했습니다."),

    PLACE_NOT_FOUND(HttpStatus.NOT_FOUND, "PLACE_404_001", "장소를 찾을 수 없습니다."),
    PLACE_DUPLICATED(HttpStatus.CONFLICT, "PLACE_409_001", "이미 등록된 장소입니다."),

    REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "REPORT_404_001", "제보를 찾을 수 없습니다."),

    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW_404_001", "리뷰를 찾을 수 없습니다."),

    CRAWL_CANDIDATE_NOT_FOUND(HttpStatus.NOT_FOUND, "CRAWL_404_001", "검수 후보를 찾을 수 없습니다."),
    CRAWL_CANDIDATE_DUPLICATED(HttpStatus.CONFLICT, "CRAWL_409_001", "기존 장소와 중복된 검수 후보입니다."),

    KAKAO_API_KEY_MISSING(HttpStatus.INTERNAL_SERVER_ERROR, "KAKAO_500_001", "카카오 REST API 키가 설정되지 않았습니다."),
    KAKAO_API_REQUEST_FAILED(HttpStatus.BAD_GATEWAY, "KAKAO_502_001", "카카오 API 호출에 실패했습니다."),

    NAVER_API_KEY_MISSING(HttpStatus.INTERNAL_SERVER_ERROR, "NAVER_500_001", "네이버 API 키가 설정되지 않았습니다."),
    NAVER_API_REQUEST_FAILED(HttpStatus.BAD_GATEWAY, "NAVER_502_001", "네이버 API 호출에 실패했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public HttpStatus status() {
        return status;
    }

    public String code() {
        return code;
    }

    public String message() {
        return message;
    }
}
