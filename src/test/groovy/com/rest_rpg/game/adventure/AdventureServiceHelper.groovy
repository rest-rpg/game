package com.rest_rpg.game.adventure

import com.rest_rpg.game.adventure.model.Adventure
import com.rest_rpg.game.enemy.EnemyServiceHelper
import com.rest_rpg.game.occupation.OccupationRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AdventureServiceHelper {

    @Autowired
    AdventureRepository adventureRepository

    @Autowired
    OccupationRepository occupationRepository

    @Autowired
    EnemyServiceHelper enemyServiceHelper

    def clean() {
        occupationRepository.deleteAll()
        adventureRepository.deleteAll()
        enemyServiceHelper.clean()
    }

    Adventure saveAdventure(Map customArgs = [:]) {
        Map args = [
                name                    : "Kill bear",
                adventureLengthInMinutes: 90,
                xpForAdventure          : 100,
                goldForAdventure        : 110,
                enemy                   : enemyServiceHelper.saveEnemy()
        ]
        args << customArgs

        def adventure = Adventure.builder()
                .name(args.name)
                .adventureTimeInMinutes(args.adventureLengthInMinutes)
                .xpForAdventure(args.xpForAdventure)
                .goldForAdventure(args.goldForAdventure)
                .enemy(args.enemy)
                .build()

        adventureRepository.save(adventure)
    }

    Adventure getAdventure(long adventureId) {
        adventureRepository.findById(adventureId).get()
    }
}
