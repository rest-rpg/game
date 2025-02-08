package com.rest_rpg.game.fight

import com.rest_rpg.game.enemy.EnemyHelper
import com.rest_rpg.game.fight.model.Fight
import com.rest_rpg.game.fight_effect.FightEffect
import org.openapitools.model.FightDetails
import org.openapitools.model.FightEffectLite

class FightHelper {

    static boolean compare(Fight fight, FightDetails details) {
        assert fight.id == details.id
        assert fight.enemyCurrentHp == details.enemyCurrentHp
        assert fight.enemyCurrentMana == details.enemyCurrentMana
        assert fight.active == details.active
        fight.enemy == null || EnemyHelper.compare(fight.enemy, details.enemy)
        compare(fight.fightEffects, details.fightEffects)

        true
    }

    static boolean compare(FightEffect effect, FightEffectLite lite) {
        assert effect.id == lite.id
        assert effect.skillEffect == lite.skillEffect
        assert effect.duration == lite.duration
        assert effect.playerEffect == lite.playerEffect

        true
    }

    static boolean compare(Collection<FightEffect> effects, List<FightEffectLite> lites) {
        assert effects.size() == lites.size()
        effects = effects.sort { it.id }
        lites = lites.sort { it.id }
        assert effects.withIndex().every { compare(it.v1, lites[it.v2]) }

        true
    }
}
