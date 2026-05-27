package com.study.yagoomap.domain.place.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:yagoomap-controller-error-test;MODE=MySQL;DATABASE_TO_LOWER=TRUE",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class PlaceControllerErrorTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void returnsPlaceNotFoundError() throws Exception {
        mockMvc.perform(get("/api/places/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PLACE_404_001"))
                .andExpect(jsonPath("$.message").value("장소를 찾을 수 없습니다."))
                .andExpect(jsonPath("$.path").value("/api/places/99999"));
    }

    @Test
    void returnsDuplicatePlaceError() throws Exception {
        mockMvc.perform(post("/api/places")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "잠실 야구포차",
                                  "address": "서울 송파구 올림픽로 25",
                                  "latitude": 37.5122,
                                  "longitude": 127.0719,
                                  "teamId": 1,
                                  "team": "LG 트윈스",
                                  "category": "포차",
                                  "phone": "02-9999-0000",
                                  "naverMapUrl": "https://map.naver.com/",
                                  "photos": [],
                                  "note": "duplicate",
                                  "tags": [],
                                  "status": "ACTIVE"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("PLACE_409_001"))
                .andExpect(jsonPath("$.message").value("가게명과 주소가 기존 장소와 같습니다."))
                .andExpect(jsonPath("$.path").value("/api/places"));
    }

    @Test
    void returnsValidationError() throws Exception {
        mockMvc.perform(post("/api/places/1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "content": "",
                                  "rating": 6
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("COMMON_400_001"))
                .andExpect(jsonPath("$.errors.length()").value(2));
    }
}
