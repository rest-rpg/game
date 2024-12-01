package com.rest_rpg.game.statistics;

import lombok.RequiredArgsConstructor;
import org.openapitools.api.StatisticsApi;
import org.openapitools.model.StatisticsDetails;
import org.openapitools.model.StatisticsUpdateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
public class StatisticsController implements StatisticsApi {

    private final StatisticsService statisticsService;

    @Override
    public ResponseEntity<StatisticsDetails> getStatistics(Long characterId) {
        return ResponseEntity.ok(statisticsService.getStatistics(characterId));
    }

    @Override
    public ResponseEntity<StatisticsDetails> trainCharacter(Long characterId, StatisticsUpdateRequest statisticsUpdateRequest) {
        return ResponseEntity.ok(statisticsService.trainCharacter(characterId, statisticsUpdateRequest));
    }
}
