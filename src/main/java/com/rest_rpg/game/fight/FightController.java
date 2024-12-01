package com.rest_rpg.game.fight;

import lombok.RequiredArgsConstructor;
import org.openapitools.api.FightApi;
import org.openapitools.model.FightActionRequest;
import org.openapitools.model.FightActionResponse;
import org.openapitools.model.FightDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
public class FightController implements FightApi {

    private final FightService fightService;

    @Override
    public ResponseEntity<FightDetails> getFight(Long characterId) {
        return ResponseEntity.ok(fightService.getFight(characterId));
    }

    @Override
    public ResponseEntity<FightActionResponse> performActionInFight(FightActionRequest fightActionRequest) {
        return ResponseEntity.ok(fightService.performActionInFight(fightActionRequest));
    }
}
