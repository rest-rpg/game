package com.rest_rpg.game.enemy

import com.rest_rpg.game.enemy.model.Enemy
import com.rest_rpg.game.enemy.model.StrategyElement
import com.rest_rpg.game.skill.SkillHelper
import com.rest_rpg.game.skill.model.Skill
import org.openapitools.model.ElementAction
import org.openapitools.model.ElementEvent
import org.openapitools.model.EnemyBasic
import org.openapitools.model.EnemyCreateRequest
import org.openapitools.model.EnemyLite
import org.openapitools.model.EnemyLites
import org.openapitools.model.StrategyElementCreateRequest

class EnemyHelper {

    static EnemyCreateRequest createEnemyCreateRequest(Skill skill, Map customArgs = [:]) {
        Map args = [
                name           : "Bear",
                hp             : 200,
                mana           : 200,
                damage         : 20,
                numberOfPotions: 2,
                skillLevel     : 1,
                enemyStrategy  : [createStrategyElementCreateRequest()]
        ]

        args << customArgs

        return new EnemyCreateRequest(args.name, args.hp, args.mana, args.damage, args.numberOfPotions,
                skill.id, args.skillLevel, args.enemyStrategy)
    }

    static StrategyElementCreateRequest createStrategyElementCreateRequest(Map customArgs = [:]) {
        Map args = [
                event   : ElementEvent.ENEMY_HEALTH_20_40,
                action  : ElementAction.NORMAL_ATTACK,
                priority: 1
        ]
        args << customArgs

        return new StrategyElementCreateRequest(args.event, args.action, args.priority)
    }

    static StrategyElement createStrategyElement(Map customArgs = [:]) {
        Map args = [
                elementEvent : ElementEvent.ENEMY_HEALTH_20_40,
                elementAction: ElementAction.NORMAL_ATTACK,
                priority     : 1
        ]
        args << customArgs

        return StrategyElement.builder()
                .elementAction(args.elementAction)
                .elementEvent(args.elementEvent)
                .priority(args.priority)
                .build()
    }

    static boolean compare(EnemyCreateRequest request, EnemyLite enemyLite) {
        assert request.name == enemyLite.name

        true
    }

    static boolean compare(Enemy enemy, EnemyLite enemyLite) {
        assert enemy.id == enemyLite.id
        assert enemy.name == enemyLite.name

        true
    }

    static boolean compare(Enemy enemy, EnemyBasic basic) {
        assert enemy.id == basic.id
        assert enemy.name == basic.name
        assert enemy.hp == basic.hp
        assert enemy.mana == basic.mana
        assert enemy.damage == basic.damage
        assert enemy.skillLevel == basic.skillLevel
        assert enemy.numberOfPotions == basic.numberOfPotions
        assert SkillHelper.compare(enemy.skill, basic.skill)

        true
    }

    static boolean compare(Enemy enemy1, Enemy enemy2) {
        assert enemy1.id == enemy2.id
        assert enemy1.name == enemy2.name
        assert enemy1.hp == enemy2.hp
        assert enemy1.mana == enemy2.mana
        assert enemy1.damage == enemy2.damage
        assert enemy1.skillLevel == enemy2.skillLevel
        assert enemy1.numberOfPotions == enemy2.numberOfPotions

        true
    }

    static boolean compare(List<Enemy> enemies, EnemyLites enemyLites) {
        def enemyLiteList = enemyLites.content
        assert enemies.size() == enemyLiteList.size()
        enemies = enemies.sort { it.id }
        enemyLiteList = enemyLiteList.sort { it.id }
        assert enemies.withIndex().every { compare(it.v1, enemyLiteList[it.v2]) }

        true
    }
}
