package com.study.yagoomap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class YagoomapApplication {

    public static void main(String[] args) {
        SpringApplication.run(YagoomapApplication.class, args);
    }

}
