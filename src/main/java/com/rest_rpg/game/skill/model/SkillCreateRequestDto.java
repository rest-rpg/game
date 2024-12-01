package com.rest_rpg.game.skill.model;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Value;
import org.openapitools.model.CharacterClass;
import org.openapitools.model.SkillEffect;
import org.openapitools.model.SkillType;

@Value
public class SkillCreateRequestDto {

    @NotBlank
    String name;

    @PositiveOrZero
    int manaCost;

    @NotNull
    SkillType type;

    @PositiveOrZero
    float multiplier;

    @PositiveOrZero
    float multiplierPerLevel;

    @Nullable
    SkillEffect effect;

    @PositiveOrZero
    int effectDuration;

    @PositiveOrZero
    int effectDurationPerLevel;

    @PositiveOrZero
    int goldCost;

    @PositiveOrZero
    int statisticPointsCost;

    @PositiveOrZero
    float effectMultiplier;

    @PositiveOrZero
    float effectMultiplierPerLevel;

    @NotNull
    CharacterClass characterClass;
}
