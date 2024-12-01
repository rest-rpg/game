package com.rest_rpg.game.fight;

import com.rest_rpg.game.fight.model.Fight;
import com.rest_rpg.game.fight.model.FightActionRequestDto;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;
import org.openapitools.model.FightActionRequest;
import org.openapitools.model.FightDetails;
import org.openapitools.model.FightLite;

@Mapper(componentModel = "spring")
public interface FightMapper {

    FightLite toLite(@NotNull Fight source);

    FightDetails toDetails(@NotNull Fight source);

    FightActionRequestDto toDto(@NotNull FightActionRequest source);
}
