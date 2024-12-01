package com.rest_rpg.game.statistics

import com.rest_rpg.game.character.CharacterServiceHelper
import com.rest_rpg.game.configuration.TestBase
import org.openapitools.model.ErrorCodes
import org.openapitools.model.StatisticsDetails
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.parsing.Problem
import org.springframework.http.HttpStatus

class StatisticsControllerTest extends TestBase {

    def baseUrl = "/statistics"
    def statisticUrl = { long characterId -> baseUrl + "/" + characterId }
    def trainUrl = { long characterId -> baseUrl + "/" + characterId + "/train" }

    @Autowired
    CharacterServiceHelper characterServiceHelper

    void cleanup() {
        characterServiceHelper.clean()
    }

    def "should get character statistics"() {
        when:
            def character = characterServiceHelper.createCharacter([name: "Carl"])
            def response = httpGet(statisticUrl(character.getId()), StatisticsDetails)
        then:
            response.status == HttpStatus.OK
            StatisticsHelper.compare(character.statistics, response.body)
    }

    def "should not get character statistics"() {
        given:
            def character = characterServiceHelper.createCharacter([name: "Carl"])
        when:
            def response = httpGet(statisticUrl(character.getId()), Problem)
        then:
            response.status == HttpStatus.NOT_FOUND
            response.errorMessage == ErrorCodes.CHARACTER_NOT_FOUND.toString()
            1 * userInternalClient.getUsernameFromContext() >> { character.name }
    }

    def "should train character statistics"() {
        given:
            def character = characterServiceHelper.createCharacter([
                    name      : "Carl",
                    statistics: StatisticsHelper.statistics(freeStatisticPoints: 50, strength: 10),
            ])
            def request = StatisticsHelper.createStatisticsUpdateRequest(
                    strength: 10,
                    dexterity: 10,
                    constitution: 10,
                    intelligence: 10)
        when:
            def response = httpPost(trainUrl(character.getId()), request, StatisticsDetails)
            character = characterServiceHelper.getCharacter(character.id)
        then:
            response.status == HttpStatus.OK
            StatisticsHelper.compare(character.statistics, response.body)
            character.statistics.strength == 20
    }

    def "should not train character statistics"() {
        given:
            def character = characterServiceHelper.createCharacter([
                    name      : "Carl",
                    statistics: StatisticsHelper.statistics(freeStatisticPoints: 39, strength: 10),
            ])
            def request = StatisticsHelper.createStatisticsUpdateRequest(
                    strength: 10,
                    dexterity: 10,
                    constitution: 10,
                    intelligence: 10)
        when:
            def response = httpPost(trainUrl(character.getId()), request, StatisticsDetails)
        then:
            response.status == HttpStatus.FORBIDDEN
            response.errorMessage == ErrorCodes.NOT_ENOUGH_SKILL_POINTS.toString()
    }
}
