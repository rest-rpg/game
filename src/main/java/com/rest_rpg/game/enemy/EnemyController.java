package com.rest_rpg.game.enemy;

import lombok.RequiredArgsConstructor;
import org.openapitools.api.EnemyApi;
import org.openapitools.model.EnemyCreateRequest;
import org.openapitools.model.EnemyLite;
import org.openapitools.model.EnemyLites;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
public class EnemyController implements EnemyApi {

    private final EnemyService enemyService;

    @Override
    public ResponseEntity<EnemyLite> createEnemy(EnemyCreateRequest enemyCreateRequest) {
        return ResponseEntity.ok(enemyService.createEnemy(enemyCreateRequest));
    }

    @Override
    public ResponseEntity<EnemyLites> getEnemies() {
        return ResponseEntity.ok(enemyService.getEnemies());
    }
}
