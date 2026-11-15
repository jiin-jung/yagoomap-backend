package com.study.yagoomap.domain.place.controller;

import com.study.yagoomap.domain.place.service.NaverImageClient;
import com.study.yagoomap.domain.place.service.PlaceService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PlaceControllerTest {

    @Test
    void bulkImagesDeduplicatesQueriesAndKeepsInsertionOrder() {
        PlaceService placeService = mock(PlaceService.class);
        NaverImageClient imageClient = mock(NaverImageClient.class);
        var first = new NaverImageClient.Image("first", "", "", "", "");
        var second = new NaverImageClient.Image("second", "", "", "", "");
        when(imageClient.search("first")).thenReturn(new NaverImageClient.ImageSearchResponse("first", List.of(first)));
        when(imageClient.search("second")).thenReturn(new NaverImageClient.ImageSearchResponse("second", List.of(second)));

        var result = new PlaceController(placeService, imageClient)
                .imagesBulk(List.of("first", "second", "first"));

        assertThat(result.keySet()).containsExactly("first", "second");
        assertThat(result.get("first")).containsExactly(first);
        verify(imageClient).search("first");
        verify(imageClient).search("second");
    }
}
