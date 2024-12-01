package com.rest_rpg.game.fight.model;

import jakarta.validation.constraints.NotNull;
import lombok.Value;
import org.openapitools.model.ElementAction;

@Value
public class FightActionRequestDto {

    long characterId;

    @NotNull
    ElementAction action;

    Long skillId;
}
