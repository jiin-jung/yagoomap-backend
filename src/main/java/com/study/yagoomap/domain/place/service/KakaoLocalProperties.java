package com.study.yagoomap.domain.place.service;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kakao.local")
public record KakaoLocalProperties(
        String baseUrl,
        String restApiKey
) {
}
