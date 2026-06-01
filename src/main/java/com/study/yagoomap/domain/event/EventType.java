package com.study.yagoomap.domain.event;

/**
 * 프론트엔드가 전송하는 사용자 행동 이벤트 종류.
 * 백엔드 enum 과 프론트 sendEvent 타입 문자열을 1:1 로 맞춘다.
 */
public enum EventType {
    PAGE_VIEW,
    SEARCH,
    VIEW_VENUE,
    FILTER_TEAM,
    CLICK_KAKAO,
    CLICK_NAVER,
    SUBMIT_REPORT,
    WRITE_REVIEW,
    SHARE
}
