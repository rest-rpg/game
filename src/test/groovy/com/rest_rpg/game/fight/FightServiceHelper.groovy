package com.rest_rpg.game.fight

import com.rest_rpg.game.fight.model.Fight
import com.rest_rpg.game.fight_effect.FightEffect
import com.rest_rpg.game.fight_effect.FightEffectRepository
import org.openapitools.model.SkillEffect
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class FightServiceHelper {

    @Autowired
    FightRepository fightRepository

    @Autowired
    FightEffectRepository fightEffectRepository

    def clean() {
        fightEffectRepository.deleteAll()
        fightRepository.deleteAll()
        fightRepository.deleteAll()
    }

    FightEffect saveFightEffect(Map customArgs = [:]) {
        Map args = [
                skillEffect     : SkillEffect.BLEEDING,
                duration        : 3,
                effectMultiplier: 0.2,
                playerEffect    : true
        ]

        args << customArgs
        def effect = FightEffect.builder()
                .skillEffect(args.skillEffect)
                .duration(args.duration)
                .isPlayerEffect(args.playerEffect)
                .effectMultiplier(args.effectMultiplier)
                .fight(args.fight as Fight)
                .build()
        return fightEffectRepository.save(effect)
    }

    Fight save(Fight fight) {
        def f = fightRepository.save(fight)
        fightRepository.readById(f.id).get()
    }

    Fight getById(long id) {
        fightRepository.readById(id).get()
    }
}
