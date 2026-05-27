package com.study.yagoomap.domain.place.service;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "naver.image")
public record NaverImageProperties(
        String baseUrl,
        String clientId,
        String clientSecret
) {
}
