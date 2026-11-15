package com.study.yagoomap.domain.place.service;

import com.study.yagoomap.domain.place.repository.PlaceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class PlaceSeederTest {

    @Test
    void skipsSampleDataInProduction() {
        PlaceRepository placeRepository = mock(PlaceRepository.class);
        PlaceService placeService = mock(PlaceService.class);
        Environment environment = mock(Environment.class);
        when(environment.acceptsProfiles(any(Profiles.class))).thenReturn(true);

        new PlaceSeeder(placeRepository, placeService, environment, false).seed();

        verifyNoInteractions(placeRepository, placeService);
    }
}
