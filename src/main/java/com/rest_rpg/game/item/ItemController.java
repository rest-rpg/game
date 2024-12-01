package com.rest_rpg.game.item;

import lombok.RequiredArgsConstructor;
import org.openapitools.api.ItemApi;
import org.openapitools.model.ItemCreateRequest;
import org.openapitools.model.ItemLite;
import org.openapitools.model.ItemLitePage;
import org.openapitools.model.ItemSearchRequest;
import org.openapitools.model.PotionLite;
import org.openapitools.model.StatisticsLite;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
public class ItemController implements ItemApi {

    private final ItemService itemService;

    @Override
    public ResponseEntity<ItemLite> createItem(ItemCreateRequest itemCreateRequest) {
        return ResponseEntity.ok(itemService.createItem(itemCreateRequest));
    }

    @Override
    public ResponseEntity<ItemLitePage> findItems(ItemSearchRequest itemSearchRequest) {
        return ResponseEntity.ok(itemService.findItems(itemSearchRequest));
    }

    @Override
    public ResponseEntity<ItemLite> buyItem(Long itemId, Long characterId) {
        return ResponseEntity.ok(itemService.buyItem(itemId, characterId));
    }

    @Override
    public ResponseEntity<Void> buyPotion(Long characterId) {
        itemService.buyPotion(characterId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<StatisticsLite> usePotion(Long characterId) {
        return ResponseEntity.ok(itemService.usePotion(characterId));
    }

    @Override
    public ResponseEntity<PotionLite> getPotionInfo() {
        return ResponseEntity.ok(itemService.getPotionInfo());
    }
}
