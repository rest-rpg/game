package com.rest_rpg.game.character;

import com.rest_rpg.game.character.model.Character;
import com.rest_rpg.game.character.model.dto.CharacterCreateRequestDto;
import com.rest_rpg.game.character_skill.CharacterSkill;
import com.rest_rpg.game.skill.SkillMapper;
import jakarta.validation.constraints.NotNull;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.openapitools.model.CharacterBasic;
import org.openapitools.model.CharacterBasics;
import org.openapitools.model.CharacterCreateRequest;
import org.openapitools.model.CharacterDetails;
import org.openapitools.model.CharacterLite;
import org.openapitools.model.SkillDetails;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface CharacterMapper {

    CharacterCreateRequestDto toDto(CharacterCreateRequest source);

    CharacterLite toLite(Character source);

    @Mapping(target = "content", source = "source")
    CharacterBasics toBasics(List<Character> source, Integer dummy);

    CharacterBasic toBasic(Character source);

    @Mapping(target = "skills", expression = "java(toDetailsList(source.getSkills()))")
    CharacterDetails toDetails(@NotNull Character source);

    default List<SkillDetails> toDetailsList(@NotNull Set<CharacterSkill> source) {
        return source.stream().map(CharacterSkill::getSkill).map(s -> Mappers.getMapper(SkillMapper.class).toDetails(s)).toList();
    }
}
