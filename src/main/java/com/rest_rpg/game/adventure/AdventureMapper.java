package com.rest_rpg.game.adventure;

import com.rest_rpg.game.adventure.model.Adventure;
import com.rest_rpg.game.adventure.model.AdventureCreateRequestDto;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;
import org.openapitools.model.AdventureBasicPage;
import org.openapitools.model.AdventureCreateRequest;
import org.openapitools.model.AdventureDetails;
import org.openapitools.model.AdventureLite;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface AdventureMapper {

    AdventureCreateRequestDto toDto(@NotNull AdventureCreateRequest source);

    AdventureLite toLite(@NotNull Adventure source);

    AdventureDetails toDetails(@NotNull Adventure source);

    AdventureBasicPage toPage(@NotNull Page<Adventure> source);
}
