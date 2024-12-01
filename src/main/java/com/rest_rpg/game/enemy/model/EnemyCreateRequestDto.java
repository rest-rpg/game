package com.rest_rpg.game.enemy.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Value;

import java.util.List;

@Value
public class EnemyCreateRequestDto {

    @NotEmpty
    String name;

    @Min(0)
    int numberOfPotions;

    @Min(1)
    int hp;

    @Min(1)
    int mana;

    @Min(1)
    int damage;

    long skillId;

    @Min(1)
    int skillLevel;

    @NotEmpty
    List<@Valid StrategyElementCreateRequestDto> strategyElementCreateRequest;
}
