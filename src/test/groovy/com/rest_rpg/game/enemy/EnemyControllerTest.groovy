package com.rest_rpg.game.enemy

import com.rest_rpg.game.configuration.TestBase
import com.rest_rpg.game.skill.SkillServiceHelper
import org.openapitools.model.ElementAction
import org.openapitools.model.ElementEvent
import org.openapitools.model.EnemyLite
import org.openapitools.model.EnemyLites
import org.openapitools.model.ErrorCodes
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus

class EnemyControllerTest extends TestBase {

    def baseUrl = "/game/enemy"

    @Autowired
    SkillServiceHelper skillServiceHelper

    @Autowired
    EnemyServiceHelper enemyServiceHelper

    @Autowired
    StrategyElementRepository strategyElementRepository

    void cleanup() {
        enemyServiceHelper.clean()
        skillServiceHelper.clean()
        strategyElementRepository.deleteAll()
    }

    def "should create enemy"() {
        given:
            def skill = skillServiceHelper.createSkill()
            def strategyRequests = [
                    EnemyHelper.createStrategyElementCreateRequest(
                            event: ElementEvent.ENEMY_HEALTH_20_40,
                            action: ElementAction.USE_POTION),
                    EnemyHelper.createStrategyElementCreateRequest(
                            event: ElementEvent.ENEMY_HEALTH_60_80,
                            action: ElementAction.NORMAL_ATTACK)
            ]
            def request = EnemyHelper.createEnemyCreateRequest(skill, [
                    name           : "Boar",
                    hp             : 100,
                    mana           : 100,
                    damage         : 30,
                    numberOfPotions: 3,
                    skillId        : 2,
                    enemyStrategy  : strategyRequests])
        when:
            def response = httpPost(baseUrl, request, EnemyLite)
        then:
            response.status == HttpStatus.OK
            EnemyHelper.compare(request, response.body)
    }

    def "should not duplicate strategies"() {
        given:
            enemyServiceHelper.saveEnemy(strategyElements: [
                    EnemyHelper.createStrategyElement(
                            elementEvent: ElementEvent.ENEMY_HEALTH_20_40,
                            elementAction: ElementAction.USE_POTION,
                            priority: 2),
                    EnemyHelper.createStrategyElement(
                            elementEvent: ElementEvent.PLAYER_HEALTH_20_40,
                            elementAction: ElementAction.SPECIAL_ATTACK,
                            priority: 1)
            ])
            def skill = skillServiceHelper.createSkill()
            def strategyRequests = [
                    EnemyHelper.createStrategyElementCreateRequest(
                            event: ElementEvent.ENEMY_HEALTH_20_40,
                            action: ElementAction.USE_POTION,
                            priority: 2),
                    EnemyHelper.createStrategyElementCreateRequest(
                            event: ElementEvent.PLAYER_HEALTH_20_40,
                            action: ElementAction.SPECIAL_ATTACK,
                            priority: 1)
            ]
            def request = EnemyHelper.createEnemyCreateRequest(skill, [name: "Boar", enemyStrategy: strategyRequests])
        when:
            def response = httpPost(baseUrl, request, EnemyLite)
        then:
            response.status == HttpStatus.OK
            EnemyHelper.compare(request, response.body)
            strategyElementRepository.findAll().size() == 2
    }

    def "should not create enemy"() {
        given:
            enemyServiceHelper.saveEnemy(name: "Bolo")
            def skill = skillServiceHelper.createSkill()
            def request = EnemyHelper.createEnemyCreateRequest(skill, [name: "Bolo"])
        when:
            def response = httpPost(baseUrl, request, EnemyLite)
        then:
            response.status == HttpStatus.CONFLICT
            response.errorMessage == ErrorCodes.ENEMY_ALREADY_EXISTS.toString()
    }

    def "should get all enemies"() {
        given:
            def enemy1 = enemyServiceHelper.saveEnemy()
            def enemy2 = enemyServiceHelper.saveEnemy()
            def enemy3 = enemyServiceHelper.saveEnemy()
            enemyServiceHelper.saveEnemy(deleted: true)
        when:
            def response = httpGet(baseUrl, EnemyLites)
        then:
            response.status == HttpStatus.OK
            EnemyHelper.compare([enemy1, enemy2, enemy3], response.body)
    }
}
