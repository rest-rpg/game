package com.rest_rpg.game.helpers

import com.rest_rpg.game.character.CharacterRepository
import com.rest_rpg.game.enemy.EnemyRepository
import com.rest_rpg.game.equipment.EquipmentRepository
import com.rest_rpg.game.fight.FightRepository
import com.rest_rpg.game.fight_effect.FightEffectRepository
import com.rest_rpg.game.item.ItemRepository
import com.rest_rpg.game.occupation.OccupationRepository
import com.rest_rpg.game.skill.SkillRepository
import com.rest_rpg.game.statistics.StatisticsRepository
import com.rest_rpg.game.work.WorkRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class DeleteServiceHelper {

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

    @Autowired
    FightEffectRepository fightEffectRepository

    @Autowired
    ItemRepository itemRepository

    @Autowired
    WorkRepository workRepository

    def clean() {
        characterRepository.deleteAll()
        enemyRepository.deleteAll()
        skillRepository.deleteAll()
        statisticsRepository.deleteAll()
        occupationRepository.deleteAll()
        fightRepository.deleteAll()
        equipmentRepository.deleteAll()
        fightEffectRepository.deleteAll()
        itemRepository.deleteAll()
        workRepository.deleteAll()
    }
}
