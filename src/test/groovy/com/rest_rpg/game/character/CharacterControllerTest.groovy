package com.rest_rpg.game.character

import com.rest_rpg.game.character.model.CharacterArtwork
import com.rest_rpg.game.configuration.TestBase
import com.rest_rpg.game.fight.model.Fight
import com.rest_rpg.game.occupation.Occupation
import com.rest_rpg.game.statistics.StatisticsHelper
import com.rest_rpg.game.statistics.StatisticsServiceHelper
import com.rest_rpg.user.api.model.Role
import com.rest_rpg.user.api.model.UserLite
import org.openapitools.model.CharacterBasics
import org.openapitools.model.CharacterDetails
import org.openapitools.model.CharacterLite
import org.openapitools.model.CharacterSex
import org.openapitools.model.ErrorCodes
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.parsing.Problem
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

import java.util.stream.Collectors

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get

class CharacterControllerTest extends TestBase {

    def baseUrl = "/character"
    def imageUrl = { String characterArtwork -> baseUrl + "/image/" + characterArtwork }
    def thumbnailUrl = { String characterArtwork -> baseUrl + "/thumbnail/" + characterArtwork }
    def artworksUrl = baseUrl + "/artworks"
    def userCharactersUrl = baseUrl + "/user-characters"
    def userCharacterUrl = { long characterId -> baseUrl + "/" + characterId }

    @Autowired
    CharacterServiceHelper characterServiceHelper

    @Autowired
    StatisticsServiceHelper statisticsServiceHelper

    void cleanup() {
        statisticsServiceHelper.clean()
        characterServiceHelper.clean()
    }

    def "should create character"() {
        given:
            def request = CharacterHelper.createCharacterCreateRequest()
        when:
            def response = httpPost(baseUrl, request, CharacterLite)
        then:
            response.status == HttpStatus.OK
            CharacterHelper.compare(request, response.body)
            def statistics = statisticsServiceHelper.getCharacterStatistics(response.body.id)
            StatisticsHelper.compare(statistics, request.statistics, response.body.race)
    }

    def "should not create character"() {
        given:
            characterServiceHelper.createCharacter([name: "Carl"])
            def request = CharacterHelper.createCharacterCreateRequest(name: "Carl")
        when:
            def response = httpPost(baseUrl, request, Problem)
        then:
            response.status == HttpStatus.CONFLICT
            response.errorMessage == ErrorCodes.CHARACTER_ALREADY_EXISTS.toString()
        when:
            request = CharacterHelper.createCharacterCreateRequest(
                    statistics: StatisticsHelper.createStatisticsUpdateRequest(strength: 100))
            response = httpPost(baseUrl, request, CharacterLite)
        then:
            response.status == HttpStatus.FORBIDDEN
            response.errorMessage == ErrorCodes.NOT_ENOUGH_SKILL_POINTS.toString()
        when:
            request = CharacterHelper.createCharacterCreateRequest(characterClass: "Demon")
            response = httpPost(baseUrl, request, CharacterLite)
        then:
            response.status == HttpStatus.NOT_FOUND
            response.errorMessage == ErrorCodes.ENUM_VALUE_NOT_FOUND.toString()
    }

    def "should get character image"() {
        when:
            def requestGet = get(imageUrl(artwork))
                    .contentType(MediaType.IMAGE_JPEG)
                    .accept(MediaType.IMAGE_JPEG)

            def response = mvc.perform(requestGet).andReturn().response
        then:
            response.status == httpStatus
        where:
            artwork                                    || httpStatus
            CharacterArtwork.HUMAN_FEMALE_1.toString() || HttpStatus.OK.value()
            "FEMALE_123"                               || HttpStatus.NOT_FOUND.value()
    }

    def "should get character thumbnail image"() {
        when:
            def requestGet = get(thumbnailUrl(artwork))
                    .contentType(MediaType.IMAGE_JPEG)
                    .accept(MediaType.IMAGE_JPEG)

            def response = mvc.perform(requestGet).andReturn().response
        then:
            response.status == httpStatus
        where:
            artwork                                    || httpStatus
            CharacterArtwork.HUMAN_FEMALE_1.toString() || HttpStatus.OK.value()
            "FEMALE_123"                               || HttpStatus.NOT_FOUND.value()
    }

    def "should get character thumbnail enum"() {
        when:
            def response = httpGet(artworksUrl, List<String>)
        then:
            response.status == HttpStatus.OK
            response.body.size() == Arrays.stream(CharacterArtwork.values()).map(Objects::toString).collect(Collectors.toList()).size()
    }

    def "should get user characters"() {
        given:
            def user = new UserLite(1l, 'User', 'email@email.com', Role.USER)
            def character1 = characterServiceHelper.createCharacter([name: "Carl", userId: user.id()])
            def character2 = characterServiceHelper.createCharacter([name: "Johnny", sex: CharacterSex.MALE, userId: user.id()])
            characterServiceHelper.createCharacter([name: "Fred", sex: CharacterSex.MALE])
            def list = [character1, character2]
        when:
            def response = httpGet(userCharactersUrl, CharacterBasics)
        then:
            response.status == HttpStatus.OK
            CharacterHelper.compare(list, response.body.content)
            1 * userInternalClient.getUserFromContext() >> { user }
    }

    def "should get user character"() {
        when:
            def character = characterServiceHelper.createCharacter([name: "Carl", occupation: Occupation.builder().fight(Fight.builder().isActive(true).build()).build()])
            def response = httpGet(userCharacterUrl(character.getId()), CharacterDetails)
        then:
            response.status == HttpStatus.OK
            CharacterHelper.compare(character, response.body)
    }

    def "should not get another user character"() {
        given:
            def character = characterServiceHelper.createCharacter([name: "Carl"])
        when:
            def response = httpGet(userCharacterUrl(character.getId()), Problem)
        then:
            response.status == HttpStatus.NOT_FOUND
            response.errorMessage == ErrorCodes.CHARACTER_NOT_FOUND.toString()
            1 * userInternalClient.getUsernameFromContext() >> { character.name }
    }
}
