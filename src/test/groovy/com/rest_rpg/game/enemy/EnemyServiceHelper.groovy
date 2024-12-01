package com.rest_rpg.game.enemy

import com.rest_rpg.game.enemy.model.Enemy
import com.rest_rpg.game.skill.SkillServiceHelper
import org.openapitools.model.ElementAction
import org.openapitools.model.ElementEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

import java.time.LocalDateTime

@Service
class EnemyServiceHelper {

    @Autowired
    SkillServiceHelper skillServiceHelper

    @Autowired
    EnemyRepository enemyRepository

    @Autowired
    StrategyElementRepository elementRepository

    def clean() {
        enemyRepository.deleteAll()
        elementRepository.deleteAll()
        skillServiceHelper.clean()
    }

    Enemy saveEnemy(Map customArgs = [:]) {
        Map args = [
                name            : "Bear" + LocalDateTime.now().getNano(),
                hp              : 200,
                mana            : 200,
                damage          : 20,
                numberOfPotions : 2,
                skill           : skillServiceHelper.createSkill(),
                skillLevel      : 1,
                strategyElements: [EnemyHelper.createStrategyElement(elementEvent: ElementEvent.ENEMY_HEALTH_0_20, elementAction: ElementAction.USE_POTION),
                                   EnemyHelper.createStrategyElement(elementEvent: ElementEvent.ENEMY_HEALTH_20_40, elementAction: ElementAction.USE_POTION),
                                   EnemyHelper.createStrategyElement(elementEvent: ElementEvent.ENEMY_HEALTH_40_60, elementAction: ElementAction.NORMAL_ATTACK),
                                   EnemyHelper.createStrategyElement(elementEvent: ElementEvent.ENEMY_HEALTH_60_80, elementAction: ElementAction.SPECIAL_ATTACK),
                                   EnemyHelper.createStrategyElement(elementEvent: ElementEvent.ENEMY_HEALTH_80_100, elementAction: ElementAction.NORMAL_ATTACK),
                                   EnemyHelper.createStrategyElement(elementEvent: ElementEvent.PLAYER_HEALTH_0_20, elementAction: ElementAction.SPECIAL_ATTACK),
                                   EnemyHelper.createStrategyElement(elementEvent: ElementEvent.PLAYER_HEALTH_20_40, elementAction: ElementAction.NORMAL_ATTACK),
                                   EnemyHelper.createStrategyElement(elementEvent: ElementEvent.PLAYER_HEALTH_40_60, elementAction: ElementAction.SPECIAL_ATTACK),
                                   EnemyHelper.createStrategyElement(elementEvent: ElementEvent.PLAYER_HEALTH_60_80, elementAction: ElementAction.SPECIAL_ATTACK),
                                   EnemyHelper.createStrategyElement(elementEvent: ElementEvent.PLAYER_HEALTH_80_100, elementAction: ElementAction.SPECIAL_ATTACK)],
                deleted         : false
        ]

        args << customArgs
        def enemy = Enemy.builder()
                .name(args.name)
                .hp(args.hp)
                .mana(args.mana)
                .damage(args.damage)
                .numberOfPotions(args.numberOfPotions)
                .skill(args.skill)
                .skillLevel(args.skillLevel)
                .strategyElements(args.strategyElements as Set)
                .deleted(args.deleted)
                .build()
        return enemyRepository.save(enemy)
    }
}
