package com.study.yagoomap.domain.game;

/**
 * 경기 상태. 네이버 스포츠 statusCode/statusInfo/cancel 조합을 정규화한다.
 */
public enum GameStatus {
    SCHEDULED("예정"),
    LIVE("경기중"),
    FINISHED("종료"),
    CANCELED("취소");

    private final String label;

    GameStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /**
     * 네이버 응답을 우리 상태로 정규화.
     * 종료(RESULT) 판정을 라이브 라벨("9회말")보다 먼저 한다 — 종료 경기도 statusInfo가 회차로 올 수 있음.
     */
    public static GameStatus from(String statusCode, String statusInfo, boolean cancel) {
        if (cancel) {
            return CANCELED;
        }
        if (statusInfo != null && statusInfo.contains("취소")) {
            return CANCELED;
        }
        String sc = statusCode == null ? "" : statusCode.trim().toUpperCase();
        if (sc.equals("RESULT") || sc.equals("FINAL") || sc.equals("END")
                || (statusInfo != null && statusInfo.contains("종료"))) {
            return FINISHED;
        }
        if (sc.equals("STARTED") || sc.equals("LIVE") || sc.equals("DURING") || sc.equals("PLAYING")) {
            return LIVE;
        }
        // BEFORE/READY 및 알 수 없는 값은 예정으로
        return SCHEDULED;
    }
}
