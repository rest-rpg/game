package com.rest_rpg.game.work

import com.rest_rpg.common.error.ErrorResponse
import com.rest_rpg.game.character.CharacterServiceHelper
import com.rest_rpg.game.configuration.TestBase
import org.openapitools.model.ErrorCodes
import org.openapitools.model.WorkLite
import org.openapitools.model.WorkLitePage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus

import java.time.LocalDateTime

class WorkControllerTest extends TestBase {

    def baseUrl = "/game/work"
    def searchUrl = baseUrl + "/search"
    def startWorkUrl = { long workId, long characterId -> baseUrl + "/" + workId + "/start/" + characterId }
    def endWorkUrl = { long workId -> baseUrl + "/" + workId + "/end" }

    @Autowired
    WorkServiceHelper workServiceHelper

    @Autowired
    CharacterServiceHelper characterServiceHelper

    void cleanup() {
        characterServiceHelper.clean()
        workServiceHelper.clean()
    }

    def "should create work"() {
        given:
            def request = WorkHelper.createWorkCreateRequest()
        when:
            def response = httpPost(baseUrl, request, WorkLite)
        then:
            response.status == HttpStatus.OK
            WorkHelper.compare(request, response.body)
            WorkHelper.compare(workServiceHelper.getWork(response.body.id), response.body)
    }

    def "should find works"() {
        given:
            workServiceHelper.saveWork(name: "Name")
            def work1 = workServiceHelper.saveWork(name: "Chop trees")
            def work2 = workServiceHelper.saveWork(name: "Chop more trees")
        and:
            def request = WorkHelper.createWorkSearchRequest(nameLike: "Chop")
        when:
            def response = httpPost(searchUrl, request, WorkLitePage)
        then:
            response.status == HttpStatus.OK
            response.body.numberOfElements == 2
            WorkHelper.compare([work1, work2], response.body)
    }

    def "should start work"() {
        given:
            def work = workServiceHelper.saveWork()
            def character = characterServiceHelper.createCharacter()
        when:
            def response = httpGet(startWorkUrl(work.id, character.id), Void)
        then:
            response.status == HttpStatus.NO_CONTENT
            characterServiceHelper.getCharacter(character.id).with {
                WorkHelper.compare(it.occupation.work, work)
                it.occupation.isOccupied()
            }
    }

    def "should not start work"() {
        given:
            def work = workServiceHelper.saveWork()
            def character = characterServiceHelper.createCharacter()
            character.occupation.setWork(work)
            character.occupation.setFinishTime(LocalDateTime.now().plusDays(1))
            character = characterServiceHelper.save(character)
        when:
            def response = httpGet(startWorkUrl(work.id, character.id), ErrorResponse)
        then:
            response.status == HttpStatus.CONFLICT
            response.body.message() == ErrorCodes.CHARACTER_IS_OCCUPIED.toString()
    }

    def "should end work"() {
        given:
            def work = workServiceHelper.saveWork(wage: 100)
            def character = characterServiceHelper.createCharacter()
            character.occupation.setWork(work)
            character.occupation.setFinishTime(LocalDateTime.now().minusDays(1))
            character.equipment.setGold(100)
            character = characterServiceHelper.save(character)
        when:
            def response = httpGet(endWorkUrl(character.id), Void)
        then:
            response.status == HttpStatus.NO_CONTENT
            characterServiceHelper.getCharacter(character.id).with {
                it.equipment.gold == 200
                !it.occupation.isOccupied()
            }
    }

    def "should not end work"() {
        given:
            def work = workServiceHelper.saveWork()
            def character = characterServiceHelper.createCharacter()
            character.occupation.setWork(work)
            character.occupation.setFinishTime(LocalDateTime.now().plusDays(1))
            character = characterServiceHelper.save(character)
        when:
            def response = httpGet(endWorkUrl(character.id), ErrorResponse)
        then:
            response.status == HttpStatus.CONFLICT
            response.body.message() == ErrorCodes.CHARACTER_STILL_WORKING.toString()
    }
}
