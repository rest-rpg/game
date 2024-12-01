package com.rest_rpg.game.character

import com.rest_rpg.game.character.model.Character
import com.rest_rpg.game.character.model.CharacterArtwork
import com.rest_rpg.game.character_skill.CharacterSkill
import com.rest_rpg.game.enemy.EnemyRepository
import com.rest_rpg.game.equipment.Equipment
import com.rest_rpg.game.equipment.EquipmentRepository
import com.rest_rpg.game.fight.FightRepository
import com.rest_rpg.game.fight.model.Fight
import com.rest_rpg.game.occupation.Occupation
import com.rest_rpg.game.occupation.OccupationRepository
import com.rest_rpg.game.skill.SkillRepository
import com.rest_rpg.game.skill.model.Skill
import com.rest_rpg.game.statistics.StatisticsHelper
import com.rest_rpg.game.statistics.StatisticsRepository
import org.openapitools.model.CharacterClass
import org.openapitools.model.CharacterRace
import org.openapitools.model.CharacterSex
import org.openapitools.model.CharacterStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class CharacterServiceHelper {

    @Autowired
    CharacterRepository characterRepository

    @Autowired
    SkillRepository skillRepository

    @Autowired
    EnemyRepository enemyRepository

    @Autowired
    StatisticsRepository statisticsRepository

    @Autowired
    OccupationRepository occupationRepository

    @Autowired
    FightRepository fightRepository

    @Autowired
    EquipmentRepository equipmentRepository

    def clean() {
        characterRepository.deleteAll()
        enemyRepository.deleteAll()
        skillRepository.deleteAll()
        statisticsRepository.deleteAll()
        occupationRepository.deleteAll()
        fightRepository.deleteAll()
        equipmentRepository.deleteAll()
    }

    Character createCharacter(Map customArgs = [:]) {
        Random random = new Random()
        Map args = [
                name          : "John" + Math.random().toString(),
                race          : CharacterRace.HUMAN,
                sex           : CharacterSex.FEMALE,
                characterClass: CharacterClass.WARRIOR,
                status        : CharacterStatus.IDLE,
                artwork       : CharacterArtwork.HUMAN_FEMALE_1,
                skills        : new HashSet<Skill>(),
                statistics    : StatisticsHelper.statistics(),
                occupation    : Occupation.builder().fight(new Fight()).build(),
                equipment     : Equipment.builder().gold(1000).build(),
                userId        : random.nextLong(100) + 1
        ]
        args << customArgs

        def character = Character.builder()
                .name(args.name)
                .characterClass(args.characterClass)
                .artwork(args.artwork)
                .race(args.race)
                .sex(args.sex)
                .equipment(args.equipment)
                .occupation(args.occupation)
                .statistics(args.statistics)
                .skills(new HashSet<CharacterSkill>())
                .status(args.status)
                .userId(args.userId)
                .build()

        character.occupation.setCharacter(character)
        character.statistics.setCharacter(character)
        character.getOccupation().getFight().setOccupation(character.occupation)
        character.equipment.setCharacter(character)

        character = save(character)

        args.skills.forEach {
            character.learnNewSkill(it)
        }

        save(character)
    }

    Character save(Character character) {
        characterRepository.save(character)
    }

    Character getCharacter(long characterId) {
        return characterRepository.getCharacterWithTestGraphById(characterId)
    }
}
