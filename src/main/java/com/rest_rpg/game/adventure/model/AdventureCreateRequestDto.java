package com.rest_rpg.game.adventure.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Value;

@Value
public class AdventureCreateRequestDto {

    @NotBlank
    String name;

    @Positive
    int adventureLengthInMinutes;

    @Positive
    int xpForAdventure;

    @Positive
    int goldForAdventure;

    long enemy;
}
