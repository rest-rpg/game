package com.rest_rpg.game.character

import com.rest_rpg.game.character.model.Character
import com.rest_rpg.game.equipment.EquipmentHelper
import com.rest_rpg.game.skill.SkillHelper
import com.rest_rpg.game.statistics.StatisticsHelper
import org.openapitools.model.CharacterArtwork
import org.openapitools.model.CharacterBasic
import org.openapitools.model.CharacterClass
import org.openapitools.model.CharacterCreateRequest
import org.openapitools.model.CharacterDetails
import org.openapitools.model.CharacterLite
import org.openapitools.model.CharacterRace
import org.openapitools.model.CharacterSex

class CharacterHelper {

    static CharacterCreateRequest createCharacterCreateRequest(Map customArgs = [:]) {
        Map args = [
                name          : "John" + Math.random(),
                race          : CharacterRace.HUMAN,
                sex           : CharacterSex.FEMALE,
                characterClass: CharacterClass.WARRIOR,
                artwork       : CharacterArtwork.HUMAN_FEMALE_1,
                statistics    : StatisticsHelper.createStatisticsUpdateRequest()
        ]

        args << customArgs

        return new CharacterCreateRequest(args.name, args.race, args.sex, args.characterClass, args.artwork, args.statistics)
    }

    static boolean compare(CharacterCreateRequest request, CharacterLite characterLite) {
        assert request.sex == characterLite.sex
        assert request.artwork == characterLite.artwork
        assert request.characterClass == characterLite.characterClass
        assert request.race == characterLite.race
        assert request.name == characterLite.name

        true
    }

    static boolean compare(Character character, CharacterBasic dto) {
        assert character.id == dto.id
        assert character.sex == dto.sex
        assert character.artwork == dto.artwork
        assert character.characterClass == dto.characterClass
        assert character.race == dto.race
        assert character.name == dto.name
        assert character.statistics.currentXp == dto.statistics.currentXp
        assert character.statistics.xpToNextLevel == dto.statistics.xpToNextLevel
        assert character.statistics.currentLevel == dto.statistics.currentLevel
        assert character.occupation?.finishTime == dto.occupation?.finishTime
        assert character.occupation?.occupationType == dto.occupation?.occupationType

        true
    }

    static boolean compare(Character character, CharacterDetails dto) {
        assert character.id == dto.id
        assert character.sex == dto.sex
        assert character.artwork == dto.artwork
        assert character.characterClass == dto.characterClass
        assert character.race == dto.race
        assert character.name == dto.name
        assert character.statistics.currentXp == dto.statistics.currentXp
        assert character.statistics.xpToNextLevel == dto.statistics.xpToNextLevel
        assert character.statistics.currentLevel == dto.statistics.currentLevel
        assert character.occupation?.finishTime == dto.occupation?.finishTime
        assert character.occupation?.occupationType == dto.occupation?.occupationType
        def skills = character.skills.skill
        assert SkillHelper.compare(skills, dto.skills)
        assert EquipmentHelper.compare(character.equipment, dto.equipment)

        true
    }

    static boolean compare(List<Character> characters, List<CharacterBasic> dtos) {
        assert characters.size() == dtos.size()
        characters = characters.sort { it.name }
        dtos = dtos.sort { it.name }
        assert characters.withIndex().every { compare(it.v1, dtos[it.v2]) }

        true
    }
}
