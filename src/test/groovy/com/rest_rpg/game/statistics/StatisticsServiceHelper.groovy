package com.rest_rpg.game.statistics

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class StatisticsServiceHelper {

    @Autowired
    StatisticsRepository statisticsRepository

    def clean() {
        statisticsRepository.deleteAll()
    }

    def getCharacterStatistics(long characterId) {
        return statisticsRepository.getStatisticsByCharacterId(characterId)
    }
}
