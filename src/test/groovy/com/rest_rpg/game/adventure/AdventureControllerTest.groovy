package com.rest_rpg.game.adventure

import com.rest_rpg.game.character.CharacterServiceHelper
import com.rest_rpg.game.configuration.TestBase
import com.rest_rpg.game.enemy.EnemyServiceHelper
import org.openapitools.model.AdventureBasicPage
import org.openapitools.model.AdventureDetails
import org.openapitools.model.AdventureLite
import org.openapitools.model.ErrorCodes
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.parsing.Problem
import org.springframework.http.HttpStatus

import java.time.LocalDateTime

class AdventureControllerTest extends TestBase {

    def baseUrl = "/game/adventure"
    def adventureUrl = { long adventureId -> baseUrl + "/" + adventureId }
    def searchUrl = baseUrl + "/search"
    def startAdventureUrl = { long adventureId, long characterId -> baseUrl + "/" + adventureId + "/start/" + characterId }
    def endAdventureUrl = { long adventureId -> baseUrl + "/" + adventureId + "/end" }

    @Autowired
    EnemyServiceHelper enemyServiceHelper

    @Autowired
    AdventureServiceHelper adventureServiceHelper

    @Autowired
    CharacterServiceHelper characterServiceHelper

    void cleanup() {
        adventureServiceHelper.clean()
        enemyServiceHelper.clean()
        characterServiceHelper.clean()
    }

    def "should create adventure"() {
        given:
            def enemy = enemyServiceHelper.saveEnemy()
        and:
            def request = AdventureHelper.createAdventureCreateRequest(enemy)
        when:
            def response = httpPost(baseUrl, request, AdventureLite)
        then:
            response.status == HttpStatus.OK
            AdventureHelper.compare(request, response.body)
    }

    def "should edit adventure"() {
        given:
            def adventure = adventureServiceHelper.saveAdventure(name: "Adventure 1")
            def enemy = enemyServiceHelper.saveEnemy()
        and:
            def request = AdventureHelper.createAdventureCreateRequest(enemy, [name: "Modified adventure"])
        when:
            def response = httpPut(adventureUrl(adventure.id), request, AdventureLite)
        then:
            response.status == HttpStatus.OK
            AdventureHelper.compare(request, response.body)
            adventure.id == response.body.id
    }

    def "should delete adventure"() {
        given:
            def adventure = adventureServiceHelper.saveAdventure()
        when:
            def response = httpDelete(adventureUrl(adventure.id), AdventureLite)
            adventure = adventureServiceHelper.getAdventure(adventure.id)
        then:
            response.status == HttpStatus.OK
            adventure.id == response.body.id
            adventure.deleted
    }

    def "should not create adventure"() {
        given:
            adventureServiceHelper.saveAdventure(name: "Adventure 1")
            def enemy = enemyServiceHelper.saveEnemy()
        and:
            def request = AdventureHelper.createAdventureCreateRequest(enemy, [name: "Adventure 1"])
        when:
            def response = httpPost(baseUrl, request, Problem)
        then:
            response.status == HttpStatus.CONFLICT
            response.errorMessage == ErrorCodes.ADVENTURE_NAME_EXISTS.toString()
    }

    def "should find adventures"() {
        given:
            adventureServiceHelper.saveAdventure(name: "Kill troll")
            def adventure1 = adventureServiceHelper.saveAdventure(name: "Adventure 1")
            def adventure2 = adventureServiceHelper.saveAdventure(name: "Adventure 2")
        and:
            def request = AdventureHelper.createAdventureSearchRequest(nameLike: "Adventure")
        when:
            def response = httpPost(searchUrl, request, AdventureBasicPage)
        then:
            response.status == HttpStatus.OK
            response.body.numberOfElements == 2
            AdventureHelper.compare([adventure1, adventure2], response.body)
    }

    def "should find adventure"() {
        given:
            adventureServiceHelper.saveAdventure(name: "Kill troll")
            def adventure1 = adventureServiceHelper.saveAdventure(name: "Adventure 1")
            adventureServiceHelper.saveAdventure(name: "Adventure 2")
        when:
            def response = httpGet(adventureUrl(adventure1.id), AdventureDetails)
        then:
            response.status == HttpStatus.OK
            AdventureHelper.compare(adventure1, response.body)
    }

    def "should start adventure"() {
        given:
            def adventure = adventureServiceHelper.saveAdventure(name: "Adventure 1")
            def character = characterServiceHelper.createCharacter()
        when:
            def response = httpGet(startAdventureUrl(adventure.id, character.id), AdventureLite)
        then:
            response.status == HttpStatus.OK
            AdventureHelper.compare(adventure, response.body)
            characterServiceHelper.getCharacter(character.id).with {
                AdventureHelper.compare(it.occupation.adventure, adventure)
                it.occupation.isOccupied()
            }
    }

    def "should not start adventure"() {
        given:
            def adventure = adventureServiceHelper.saveAdventure(name: "Adventure 1")
            def character = characterServiceHelper.createCharacter()
            character.occupation.setAdventure(adventure)
            character = characterServiceHelper.save(character)
        when:
            def response = httpGet(startAdventureUrl(adventure.id, character.id), AdventureLite)
        then:
            response.status == HttpStatus.CONFLICT
            response.errorMessage == ErrorCodes.CHARACTER_IS_OCCUPIED.toString()
    }

    def "should end adventure"() {
        given:
            def adventure = adventureServiceHelper.saveAdventure(name: "Adventure 1")
            def character = characterServiceHelper.createCharacter()
            character.occupation.setAdventure(adventure)
            character.occupation.setFinishTime(LocalDateTime.now().minusDays(1))
            character = characterServiceHelper.save(character)
            character = characterServiceHelper.save(character)
        when:
            def response = httpGet(endAdventureUrl(character.id), AdventureLite)
        then:
            response.status == HttpStatus.OK
            AdventureHelper.compare(adventure, response.body)
            characterServiceHelper.getCharacter(character.id).every {
                AdventureHelper.compare(it.occupation.adventure, adventure)
                it.occupation.fight.enemy.id == adventure.enemy.id
                it.occupation.fight.active
                it.occupation.fight.enemyCurrentHp == adventure.enemy.hp
                it.occupation.fight.enemyCurrentMana == adventure.enemy.mana
            }
    }

    def "should not end adventure"() {
        given:
            def adventure = adventureServiceHelper.saveAdventure(name: "Adventure 1")
            def character = characterServiceHelper.createCharacter()
            character.occupation.setAdventure(adventure)
            character.occupation.setFinishTime(LocalDateTime.now().minusDays(1))
            character.occupation.fight.setActive(true)
            character = characterServiceHelper.save(character)
        when:
            def response = httpGet(endAdventureUrl(character.id), AdventureLite)
        then:
            response.status == HttpStatus.CONFLICT
            response.errorMessage == ErrorCodes.FIGHT_IS_ONGOING.toString()
        when:
            character.occupation.setFinishTime(LocalDateTime.now().plusDays(1))
            character.occupation.fight.setActive(false)
            character = characterServiceHelper.save(character)
            response = httpGet(endAdventureUrl(character.id), AdventureLite)
        then:
            response.status == HttpStatus.CONFLICT
            response.errorMessage == ErrorCodes.CHARACTER_STILL_ON_ADVENTURE.toString()
    }
}
