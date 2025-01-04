package com.rest_rpg.game.fight

import com.rest_rpg.game.adventure.AdventureServiceHelper
import com.rest_rpg.game.character.CharacterServiceHelper
import com.rest_rpg.game.character.model.Character
import com.rest_rpg.game.configuration.TestBase
import com.rest_rpg.game.enemy.EnemyServiceHelper
import com.rest_rpg.game.equipment.Equipment
import com.rest_rpg.game.fight.model.Fight
import com.rest_rpg.game.fight_effect.FightEffect
import com.rest_rpg.game.helpers.DeleteServiceHelper
import com.rest_rpg.game.item.ItemService
import com.rest_rpg.game.skill.SkillServiceHelper
import com.rest_rpg.game.statistics.StatisticsHelper
import org.openapitools.model.ElementAction
import org.openapitools.model.FightActionRequest
import org.openapitools.model.FightActionResponse
import org.openapitools.model.FightDetails
import org.openapitools.model.SkillEffect
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus

class FightControllerTest extends TestBase {

    def baseUrl = "/game/fight"
    def fightUrl = { long characterId -> baseUrl + "/" + characterId }

    @Autowired
    CharacterServiceHelper characterServiceHelper

    @Autowired
    FightServiceHelper fightServiceHelper

    @Autowired
    EnemyServiceHelper enemyServiceHelper

    @Autowired
    DeleteServiceHelper deleteServiceHelper

    @Autowired
    SkillServiceHelper skillServiceHelper

    @Autowired
    AdventureServiceHelper adventureServiceHelper

    void cleanup() {
        deleteServiceHelper.clean()
    }

    def "should get fight"() {
        given:
            def character = characterServiceHelper.createCharacter([name: "Carl"])
            def enemy = enemyServiceHelper.saveEnemy()
            def effects = [fightServiceHelper.saveFightEffect(fight: character.occupation.fight),
                           fightServiceHelper.saveFightEffect(fight: character.occupation.fight, isPlayerEffect: false)] as Set
            def fight = character.occupation.fight
            fight.setFightEffects(effects)
            fight.setEnemy(enemy)
            fight.setEnemyCurrentHp(100)
            fight.setEnemyCurrentMana(100)
            fight = fightServiceHelper.save(fight)
        when:
            def response = httpGet(fightUrl(character.id), FightDetails)
        then:
            response.status == HttpStatus.OK
            FightHelper.compare(fight, response.body)
    }

    def "should attack normal"() {
        given:
            def character = characterServiceHelper.createCharacter([name: "Carl", strength: 10, dexterity: 0])
            def adventure = adventureServiceHelper.saveAdventure()
            character.occupation.setAdventure(adventure)
            character = characterServiceHelper.save(character)
            def effect = [fightServiceHelper.saveFightEffect(
                    skillEffect: SkillEffect.WEAKNESS,
                    fight: character.occupation.fight,
                    effectMultiplier: 0.5,
                    duration: 3,
                    playerEffect: true)] as Set
            def fight = prepareFight(character, effect)
        and:
            def request = new FightActionRequest(character.id, ElementAction.NORMAL_ATTACK.toString())
        when:
            def response = httpPost(baseUrl, request, FightActionResponse)
        then:
            response.status == HttpStatus.OK
            FightHelper.compare(fightServiceHelper.getById(fight.id), response.body.fight)
            response.body.playerDamage == character.statistics.damage * 0.5 * (response.body.playerCriticalStrike ? 2 : 1)
    }

    def "should attack special"() {
        given:
            def skill = skillServiceHelper.createSkill(effect: SkillEffect.BLEEDING)
            def character = characterServiceHelper.createCharacter([name: "Carl", skills: [skill]])
            def adventure = adventureServiceHelper.saveAdventure()
            character.occupation.setAdventure(adventure)
            character = characterServiceHelper.save(character)
            def fight = prepareFight(character)
        and:
            def request = new FightActionRequest(character.id, ElementAction.SPECIAL_ATTACK.toString()).skillId(skill.id)
        when:
            def response = httpPost(baseUrl, request, FightActionResponse)
        then:
            response.status == HttpStatus.OK
            response.body.with {
                FightHelper.compare(fightServiceHelper.getById(fight.id), it.fight)
                assert it.playerCurrentMana == character.statistics.maxMana - skill.manaCost + character.statistics.maxMana * FightService.MANA_REGENERATION_PERCENT_PER_TURN / 100
                assert it.playerCurrentHp
                assert it.fight.fightEffects.first().skillEffect == SkillEffect.BLEEDING.toString()
            }
    }

    def "should use potion"() {
        given:
            def character = characterServiceHelper.createCharacter([
                    equipment : Equipment.builder().healthPotions(1).build(),
                    statistics: StatisticsHelper.statistics(maxHp: 100, currentHp: 50)])
            def adventure = adventureServiceHelper.saveAdventure()
            character.occupation.setAdventure(adventure)
            character = characterServiceHelper.save(character)
            def fight = prepareFight(character)
            fight.setEnemyCurrentHp(10)
            fight = fightServiceHelper.save(fight)
        and:
            def request = new FightActionRequest(character.id, ElementAction.USE_POTION.toString())
        when:
            def response = httpPost(baseUrl, request, FightActionResponse)
        then:
            response.status == HttpStatus.OK
            response.body.every {
                FightHelper.compare(fightServiceHelper.getById(fight.id), it.fight)
                it.playerPotions == 0
                it.playerCurrentHp == ItemService.POTION_HEAL_PERCENT * character.statistics.maxHp / 100 + character.statistics.currentHp
            }
    }

    def "should win fight"() {
        given:
            def character = characterServiceHelper.createCharacter([
                    statistics: StatisticsHelper.statistics(currentXp: 0, currentLevel: 1),
                    equipment : Equipment.builder().gold(0).build(),
            ])
            def adventure = adventureServiceHelper.saveAdventure()
            character.occupation.setAdventure(adventure)
            character = characterServiceHelper.save(character)
            def fight = prepareFight(character)
            fight.setEnemyCurrentHp(10)
            fight = fightServiceHelper.save(fight)
        and:
            def request = new FightActionRequest(character.id, ElementAction.NORMAL_ATTACK.toString())
        when:
            def response = httpPost(baseUrl, request, FightActionResponse)
            character = characterServiceHelper.getCharacter(character.id)
        then:
            response.status == HttpStatus.OK
            FightHelper.compare(fightServiceHelper.getById(fight.id), response.body.fight)
            response.body.playerWon == true
            character.getOccupation().adventure == null
            character.statistics.currentXp == adventure.xpForAdventure
            character.equipment.gold == adventure.goldForAdventure
    }

    def "should level up after fight"() {
        given:
            def character = characterServiceHelper.createCharacter([
                    statistics: StatisticsHelper.statistics(currentXp: 0, currentLevel: 1),
                    equipment : Equipment.builder().gold(0).build(),
            ])
            def adventure = adventureServiceHelper.saveAdventure(xpForAdventure: 2000)
            character.occupation.setAdventure(adventure)
            character = characterServiceHelper.save(character)
            def fight = prepareFight(character)
            fight.setEnemyCurrentHp(10)
            fightServiceHelper.save(fight)
        and:
            def request = new FightActionRequest(character.id, ElementAction.NORMAL_ATTACK.toString())
        when:
            def response = httpPost(baseUrl, request, FightActionResponse)
            character = characterServiceHelper.getCharacter(character.id)
        then:
            response.status == HttpStatus.OK
            response.body.playerWon == true
            character.statistics.currentLevel == 3
    }

    def "should lose fight"() {
        given:
            def character = characterServiceHelper.createCharacter([
                    statistics: StatisticsHelper.statistics(currentXp: 0, currentLevel: 1, currentHp: 0, strength: 1),
                    equipment : Equipment.builder().gold(0).build(),
            ])
            def adventure = adventureServiceHelper.saveAdventure()
            character.occupation.setAdventure(adventure)
            character = characterServiceHelper.save(character)
            def fight = prepareFight(character)
        and:
            def request = new FightActionRequest(character.id, ElementAction.NORMAL_ATTACK.toString())
        when:
            def response = httpPost(baseUrl, request, FightActionResponse)
            character = characterServiceHelper.getCharacter(character.id)
        then:
            response.status == HttpStatus.OK
            FightHelper.compare(fightServiceHelper.getById(fight.id), response.body.fight)
            response.body.playerWon == false
            character.getOccupation().adventure == null
    }

    def "should kill player due to a bleeding effect"() {
        given:
            def character = characterServiceHelper.createCharacter([
                    statistics: StatisticsHelper.statistics(currentXp: 0, currentLevel: 1, currentHp: 1, strength: 1),
                    equipment : Equipment.builder().gold(0).build(),
            ])
            def adventure = adventureServiceHelper.saveAdventure()
            character.occupation.setAdventure(adventure)
            character = characterServiceHelper.save(character)
            def bleedingEffect = [fightServiceHelper.saveFightEffect(
                    skillEffect: SkillEffect.BLEEDING,
                    fight: character.occupation.fight,
                    effectMultiplier: 0.2,
                    duration: 3,
                    playerEffect: true)] as Set
            def fight = prepareFight(character, bleedingEffect)
        and:
            def request = new FightActionRequest(character.id, ElementAction.NORMAL_ATTACK.toString())
        when:
            def response = httpPost(baseUrl, request, FightActionResponse)
            character = characterServiceHelper.getCharacter(character.id)
        then:
            response.status == HttpStatus.OK
            FightHelper.compare(fightServiceHelper.getById(fight.id), response.body.fight)
            response.body.playerWon == false
            character.getOccupation().adventure == null
    }

    def "should kill enemy due to a bleeding effect"() {
        given:
            def character = characterServiceHelper.createCharacter([
                    statistics: StatisticsHelper.statistics(currentXp: 0, currentLevel: 1, currentHp: 100, strength: 1),
                    equipment : Equipment.builder().healthPotions(1).build(),
            ])
            def adventure = adventureServiceHelper.saveAdventure()
            character.occupation.setAdventure(adventure)
            character = characterServiceHelper.save(character)
            def bleedingEffect = [fightServiceHelper.saveFightEffect(
                    skillEffect: SkillEffect.BLEEDING,
                    fight: character.occupation.fight,
                    effectMultiplier: 0.2,
                    duration: 3,
                    playerEffect: false)] as Set
            def fight = prepareFight(character, bleedingEffect)
            fight.setEnemyCurrentHp(5)
            fight = fightServiceHelper.save(fight)
        and:
            def request = new FightActionRequest(character.id, ElementAction.USE_POTION.toString())
        when:
            def response = httpPost(baseUrl, request, FightActionResponse)
        then:
            response.status == HttpStatus.OK
            FightHelper.compare(fightServiceHelper.getById(fight.id), response.body.fight)
            response.body.playerWon == true
            response.body.fight.enemyCurrentHp == 0
            response.body.fight.fightEffects.first().duration == 0
    }

    def "player should not perform an action due to being stunned"() {
        given:
            def character = characterServiceHelper.createCharacter([
                    statistics: StatisticsHelper.statistics(currentXp: 0, currentLevel: 1, currentHp: 100, strength: 1),
                    equipment : Equipment.builder().healthPotions(1).build(),
            ])
            def adventure = adventureServiceHelper.saveAdventure()
            character.occupation.setAdventure(adventure)
            character = characterServiceHelper.save(character)
            def effect = [fightServiceHelper.saveFightEffect(
                    skillEffect: SkillEffect.STUNNED,
                    fight: character.occupation.fight,
                    effectMultiplier: 0.2,
                    duration: 3,
                    playerEffect: true)] as Set
            def fight = prepareFight(character, effect)
        and:
            def request = new FightActionRequest(character.id, ElementAction.USE_POTION.toString())
        when:
            def response = httpPost(baseUrl, request, FightActionResponse)
        then:
            response.status == HttpStatus.OK
            FightHelper.compare(fightServiceHelper.getById(fight.id), response.body.fight)
            response.body.playerPotions == 1
            response.body.fight.fightEffects.first().duration == 2
    }

    def "enemy should not perform an action due to being stunned"() {
        given:
            def character = characterServiceHelper.createCharacter([
                    statistics: StatisticsHelper.statistics(currentXp: 0, currentLevel: 1, currentHp: 1, strength: 1),
                    equipment : Equipment.builder().healthPotions(1).build(),
            ])
            def adventure = adventureServiceHelper.saveAdventure()
            character.occupation.setAdventure(adventure)
            character = characterServiceHelper.save(character)
            def effect = [fightServiceHelper.saveFightEffect(
                    skillEffect: SkillEffect.STUNNED,
                    fight: character.occupation.fight,
                    duration: 3,
                    playerEffect: false)] as Set
            def fight = prepareFight(character, effect)
            fight.setEnemyCurrentHp(5)
            fight = fightServiceHelper.save(fight)
        and:
            def request = new FightActionRequest(character.id, ElementAction.USE_POTION.toString())
        when:
            def response = httpPost(baseUrl, request, FightActionResponse)
        then:
            response.status == HttpStatus.OK
            FightHelper.compare(fightServiceHelper.getById(fight.id), response.body.fight)
            response.body.fight.enemyCurrentHp == 5
    }

    private Fight prepareFight(Character character, Set<FightEffect> fightEffects = []) {
        def enemy = enemyServiceHelper.saveEnemy()
        def fight = character.occupation.fight
        fight.setFightEffects(fightEffects)
        fight.setEnemy(enemy)
        fight.setActive(true)
        fight.setEnemyCurrentHp(200)
        fight.setEnemyCurrentMana(100)
        fightServiceHelper.save(fight)
    }
}
