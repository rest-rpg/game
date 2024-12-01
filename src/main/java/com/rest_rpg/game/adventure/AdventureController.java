package com.rest_rpg.game.adventure;

import lombok.RequiredArgsConstructor;
import org.openapitools.api.AdventureApi;
import org.openapitools.model.AdventureBasicPage;
import org.openapitools.model.AdventureCreateRequest;
import org.openapitools.model.AdventureDetails;
import org.openapitools.model.AdventureLite;
import org.openapitools.model.AdventureSearchRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
public class AdventureController implements AdventureApi {

    private final AdventureService adventureService;

    @Override
    public ResponseEntity<AdventureLite> createAdventure(AdventureCreateRequest adventureCreateRequest) {
        return ResponseEntity.ok(adventureService.createAdventure(adventureCreateRequest));
    }

    @Override
    public ResponseEntity<AdventureBasicPage> findAdventures(AdventureSearchRequest adventureSearchRequest) {
        return ResponseEntity.ok(adventureService.findAdventures(adventureSearchRequest));
    }

    @Override
    public ResponseEntity<AdventureDetails> getAdventure(Long adventureId) {
        return ResponseEntity.ok(adventureService.getAdventure(adventureId));
    }

    @Override
    public ResponseEntity<AdventureLite> startAdventure(Long adventureId, Long characterId) {
        return ResponseEntity.ok(adventureService.startAdventure(adventureId, characterId));
    }

    @Override
    public ResponseEntity<AdventureLite> editAdventure(Long adventureId, AdventureCreateRequest adventureCreateRequest) {
        return ResponseEntity.ok(adventureService.editAdventure(adventureId, adventureCreateRequest));
    }

    @Override
    public ResponseEntity<AdventureLite> deleteAdventure(Long adventureId) {
        return ResponseEntity.ok(adventureService.deleteAdventure(adventureId));
    }

    @Override
    public ResponseEntity<AdventureLite> endAdventure(Long characterId) {
        return ResponseEntity.ok(adventureService.endAdventure(characterId));
    }
}
