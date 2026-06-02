package com.study.yagoomap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import java.util.TimeZone;

@SpringBootApplication
@ConfigurationPropertiesScan
public class YagoomapApplication {

    public static void main(String[] args) {
        // 서버 기본 타임존을 한국 시간(KST, UTC+9)으로 고정
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
        SpringApplication.run(YagoomapApplication.class, args);
    }

}
