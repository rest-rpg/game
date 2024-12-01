package com.rest_rpg.game.enemy.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Value;
import org.openapitools.model.ElementAction;
import org.openapitools.model.ElementEvent;

@Value
public class StrategyElementCreateRequestDto {

    @NotNull
    ElementEvent event;

    @NotNull
    ElementAction action;

    @PositiveOrZero
    int priority;
}
