package com.study.yagoomap.domain.game;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 네이버 스포츠 팀 코드 → 프론트엔드 팀 키(풀네임) 매핑.
 * 프론트 src/data/teams.js 의 key 와 정확히 일치해야 한다.
 */
public enum KboTeam {
    OB("두산 베어스"),
    HT("KIA 타이거즈"),
    SK("SSG 랜더스"),
    WO("키움 히어로즈"),
    LG("LG 트윈스"),
    KT("KT 위즈"),
    SS("삼성 라이온즈"),
    LT("롯데 자이언츠"),
    NC("NC 다이노스"),
    HH("한화 이글스");

    private final String fullName;

    KboTeam(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }

    private static final Map<String, String> BY_CODE = Arrays.stream(values())
            .collect(Collectors.toMap(Enum::name, KboTeam::getFullName));

    /**
     * 네이버 팀 코드를 프론트 팀 키로 변환. 미지의 코드는 fallbackName(네이버 표기) 그대로 반환.
     */
    public static String toKey(String code, String fallbackName) {
        if (code != null) {
            String key = BY_CODE.get(code.trim().toUpperCase());
            if (key != null) {
                return key;
            }
        }
        return fallbackName;
    }

    public static String toKey(String code) {
        return toKey(code, code);
    }
}
