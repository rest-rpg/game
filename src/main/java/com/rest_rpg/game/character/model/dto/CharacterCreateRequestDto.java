package com.rest_rpg.game.character.model.dto;

import com.rest_rpg.game.statistics.dto.StatisticsUpdateRequestDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.openapitools.model.CharacterArtwork;
import org.openapitools.model.CharacterClass;
import org.openapitools.model.CharacterRace;
import org.openapitools.model.CharacterSex;

public record CharacterCreateRequestDto(@NotNull String name, @NotNull CharacterRace race, @NotNull CharacterSex sex,
                                        @NotNull CharacterClass characterClass, @NotNull CharacterArtwork artwork,
                                        @NotNull @Valid StatisticsUpdateRequestDto statistics) {

}
