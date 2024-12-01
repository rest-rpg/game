package com.rest_rpg.game.work;

import lombok.RequiredArgsConstructor;
import org.openapitools.api.WorkApi;
import org.openapitools.model.WorkCreateRequest;
import org.openapitools.model.WorkLite;
import org.openapitools.model.WorkLitePage;
import org.openapitools.model.WorkSearchRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
public class WorkController implements WorkApi {

    private final WorkService workService;

    @Override
    public ResponseEntity<WorkLite> createWork(WorkCreateRequest workCreateRequest) {
        return ResponseEntity.ok(workService.createWork(workCreateRequest));
    }

    @Override
    public ResponseEntity<WorkLitePage> findWorks(WorkSearchRequest workSearchRequest) {
        return ResponseEntity.ok(workService.findWorks(workSearchRequest));
    }

    @Override
    public ResponseEntity<Void> startWork(Long workId, Long characterId) {
        workService.startWork(workId, characterId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> endWork(Long characterId) {
        workService.endWork(characterId);
        return ResponseEntity.noContent().build();
    }
}
