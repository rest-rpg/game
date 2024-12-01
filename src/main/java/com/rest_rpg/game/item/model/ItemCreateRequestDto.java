package com.rest_rpg.game.item.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Value;
import org.openapitools.model.ItemType;

@Value
public class ItemCreateRequestDto {

    @NotBlank
    String name;

    @NotNull
    ItemType type;

    @Positive
    int power;

    @Positive
    int price;
}
