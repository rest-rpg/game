package com.rest_rpg.game.statistics

import org.openapitools.model.CharacterRace
import org.openapitools.model.StatisticsDetails
import org.openapitools.model.StatisticsLite
import org.openapitools.model.StatisticsUpdateRequest

class StatisticsHelper {

    static StatisticsUpdateRequest createStatisticsUpdateRequest(Map customArgs = [:]) {
        Map args = [
                strength    : 2,
                dexterity   : 2,
                constitution: 3,
                intelligence: 3
        ]
        args << customArgs
        def request = new StatisticsUpdateRequest()
        request.strength(args.strength)
        request.dexterity(args.dexterity)
        request.constitution(args.constitution)
        request.intelligence(args.intelligence)

        return request
    }

    static Statistics statistics(Map customArgs = [:]) {
        Map args = [
                maxHp              : 300,
                currentHp          : 300,
                maxMana            : 100,
                currentMana        : 100,
                currentXp          : 100,
                xpToNextLevel      : 250,
                currentLevel       : 2,
                freeStatisticPoints: 10,
                strength           : 40,
                dexterity          : 30,
                constitution       : 30,
                intelligence       : 30,
                character          : null,
                deleted            : false
        ]
        args << customArgs

        return Statistics.builder()
                .maxHp(args.maxHp)
                .currentHp(args.currentHp)
                .maxMana(args.maxMana)
                .currentMana(args.currentMana)
                .currentXp(args.currentXp)
                .currentLevel(args.currentLevel)
                .freeStatisticPoints(args.freeStatisticPoints)
                .strength(args.strength)
                .dexterity(args.dexterity)
                .constitution(args.constitution)
                .intelligence(args.intelligence)
                .character(args.character)
                .deleted(args.deleted)
                .build()
    }

    static boolean compare(Statistics statistics, StatisticsLite dto) {
        assert statistics.maxHp == dto.maxHp
        assert statistics.currentHp == dto.currentHp
        assert statistics.maxMana == dto.maxMana
        assert statistics.currentMana == dto.currentMana
        assert statistics.currentXp == dto.currentXp
        assert statistics.xpToNextLevel == dto.xpToNextLevel
        assert statistics.currentLevel == dto.currentLevel

        true
    }

    static boolean compare(Statistics statistics, StatisticsDetails dto) {
        assert statistics.maxHp == dto.maxHp
        assert statistics.currentHp == dto.currentHp
        assert statistics.maxMana == dto.maxMana
        assert statistics.currentMana == dto.currentMana
        assert statistics.damage == dto.damage
        assert statistics.magicDamage == dto.magicDamage
        assert statistics.armor == dto.armor
        assert statistics.dodgeChance == dto.dodgeChance
        assert statistics.criticalChance == dto.criticalChance
        assert statistics.currentXp == dto.currentXp
        assert statistics.xpToNextLevel == dto.xpToNextLevel
        assert statistics.currentLevel == dto.currentLevel
        assert statistics.freeStatisticPoints == dto.freeStatisticPoints
        assert statistics.strength == dto.strength
        assert statistics.dexterity == dto.dexterity
        assert statistics.constitution == dto.constitution
        assert statistics.intelligence == dto.intelligence

        true
    }

    static boolean compare(Statistics statistics, StatisticsUpdateRequest dto, String characterRace) {
        CharacterRace race = CharacterRace.valueOf(characterRace)
        assert statistics.dodgeChance == criticalDodgeChance(dto.dexterity)
        assert statistics.criticalChance == criticalDodgeChance(dto.dexterity)
        assert statistics.maxHp == dto.constitution * Statistics.HP_MULTIPLIER
        assert statistics.currentHp == statistics.maxHp;
        assert statistics.maxMana == dto.intelligence * Statistics.MANA_MULTIPLIER
        assert statistics.currentMana == statistics.maxMana
        assert statistics.damage == (dto.strength + (race == CharacterRace.HUMAN ? Statistics.CHARACTER_RACE_BONUS : 0)) * Statistics.DAMAGE_MULTIPLIER
        assert statistics.magicDamage == dto.intelligence * Statistics.MAGIC_DAMAGE_MULTIPLIER
        assert statistics.armor == 0
        assert statistics.currentXp == 0
        assert statistics.xpToNextLevel == 250
        assert statistics.currentLevel == 1
        assert statistics.freeStatisticPoints == Statistics.START_FREE_STATISTICS_POINTS -
                dto.strength - dto.dexterity - dto.constitution - dto.intelligence
        assert statistics.strength == dto.strength + (race == CharacterRace.HUMAN ? Statistics.CHARACTER_RACE_BONUS : 0)
        assert statistics.dexterity == dto.dexterity + (race == CharacterRace.ELF ? Statistics.CHARACTER_RACE_BONUS : 0)
        assert statistics.constitution == dto.constitution + (race == CharacterRace.DWARF ? Statistics.CHARACTER_RACE_BONUS : 0)
        assert statistics.intelligence == dto.intelligence

        true
    }

    private static float criticalDodgeChance(int statistic) {
        double k = 0.01
        return (float) (100 * (1 - Math.exp(-k * statistic)))
    }
}
