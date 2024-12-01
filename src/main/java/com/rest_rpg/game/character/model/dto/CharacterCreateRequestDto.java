package com.rest_rpg.game.character.model.dto;

import com.rest_rpg.game.character.model.CharacterArtwork;
import com.rest_rpg.game.statistics.dto.StatisticsUpdateRequestDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Value;
import org.openapitools.model.CharacterClass;
import org.openapitools.model.CharacterRace;
import org.openapitools.model.CharacterSex;

@Value
public class CharacterCreateRequestDto {

    @NotNull
    String name;

    @NotNull
    CharacterRace race;

    @NotNull
    CharacterSex sex;

    @NotNull
    CharacterClass characterClass;

    @NotNull
    CharacterArtwork artwork;

    @NotNull
    @Valid
    StatisticsUpdateRequestDto statistics;
}
