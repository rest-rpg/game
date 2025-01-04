package com.rest_rpg.game.skill

import com.rest_rpg.game.character.CharacterServiceHelper
import com.rest_rpg.game.configuration.TestBase
import com.rest_rpg.game.helpers.DeleteServiceHelper
import org.openapitools.model.CharacterClass
import org.openapitools.model.CharacterSkillBasics
import org.openapitools.model.ErrorCodes
import org.openapitools.model.SkillBasicPage
import org.openapitools.model.SkillDetails
import org.openapitools.model.SkillLite
import org.openapitools.model.SkillLites
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus

class SkillControllerTest extends TestBase {

    def baseUrl = "/game/skill"
    def searchUrl = baseUrl + "/search"
    def skillUrl = { long skillId -> baseUrl + "/" + skillId }
    def characterSkillsUrl = { long characterId -> baseUrl + "/character/" + characterId }
    def skillLearnUrl = { long skillId, long characterId -> baseUrl + "/" + skillId + "/learn/" + characterId }

    @Autowired
    SkillServiceHelper skillServiceHelper

    @Autowired
    CharacterServiceHelper characterServiceHelper

    @Autowired
    DeleteServiceHelper deleteServiceHelper

    void cleanup() {
        deleteServiceHelper.clean()
    }

    def "should create skill"() {
        given:
            def request = SkillHelper.createSkillCreateRequest()
        when:
            def response = httpPost(baseUrl, request, SkillLite)
        then:
            response.status == HttpStatus.OK
            SkillHelper.compare(request, response.body)
    }

    def "should not create skill"() {
        given:
            skillServiceHelper.createSkill(name: "Skill")
            def request = SkillHelper.createSkillCreateRequest(name: "Skill")
        when:
            def response = httpPost(baseUrl, request, SkillLite)
        then:
            response.status == HttpStatus.CONFLICT
            response.errorMessage == ErrorCodes.SKILL_ALREADY_EXISTS.toString()
    }

    def "should find skills"() {
        given:
            skillServiceHelper.createSkill(name: "Fireball")
            def skill = skillServiceHelper.createSkill(name: "Skill")
            def skill2 = skillServiceHelper.createSkill(name: "Skill2")
            def request = SkillHelper.createSkillSearchRequest(name: "Skill")
        when:
            def response = httpPost(searchUrl, request, SkillBasicPage)
        then:
            response.status == HttpStatus.OK
            response.body.numberOfElements == 2
            SkillHelper.compare([skill, skill2], response.body)
    }

    def "should get skill"() {
        given:
            skillServiceHelper.createSkill(name: "Fireball")
            def skill = skillServiceHelper.createSkill(name: "Skill")
        when:
            def response = httpGet(skillUrl(skill.id), SkillDetails)
        then:
            response.status == HttpStatus.OK
            SkillHelper.compare(skill, response.body)
    }

    def "should get skills"() {
        given:
            def skill1 = skillServiceHelper.createSkill(name: "Fireball")
            def skill2 = skillServiceHelper.createSkill(name: "Skill")
            skillServiceHelper.createSkill(name: "Skill 2", deleted: true)
        when:
            def response = httpGet(baseUrl, SkillLites)
        then:
            response.status == HttpStatus.OK
            SkillHelper.compare([skill1, skill2], response.body)
    }

    def "should get character skills"() {
        given:
            def skill1 = skillServiceHelper.createSkill(name: "Fireball")
            def skill2 = skillServiceHelper.createSkill(name: "Skill")
            skillServiceHelper.createSkill(name: "Dash")
            def character = characterServiceHelper.createCharacter([skills: [skill1, skill2] as Set])
        when:
            def response = httpGet(characterSkillsUrl(character.id), CharacterSkillBasics)
        then:
            response.status == HttpStatus.OK
            response.body.content.size() == 2
            SkillHelper.compare([skill1, skill2], response.body)
    }

    def "should learn skill"() {
        given:
            def skill1 = skillServiceHelper.createSkill(name: "Fireball")
            skillServiceHelper.createSkill(name: "Skill")
            def character = characterServiceHelper.createCharacter()
        when:
            def response = httpGet(skillLearnUrl(skill1.id, character.id), SkillLite)
            character = characterServiceHelper.getCharacter(character.id)
        then:
            response.status == HttpStatus.OK
            SkillHelper.compare(skill1, response.body)
            character.skills.first().level == 1
            SkillHelper.compare(character.skills.first().skill, skill1)
    }

    def "should upgrade skill"() {
        given:
            def skill = skillServiceHelper.createSkill(name: "Fireball")
            def character = characterServiceHelper.createCharacter([name: "Carl", skills: [skill]])
        when:
            def response = httpGet(skillLearnUrl(skill.id, character.id), SkillLite)
            character = characterServiceHelper.getCharacter(character.id)
        then:
            response.status == HttpStatus.OK
            SkillHelper.compare(skill, response.body)
            character.skills.first().level == 2
            SkillHelper.compare(character.skills.first().skill, skill)
    }

    def "should not learn skill"() {
        given:
            def skill1 = skillServiceHelper.createSkill(name: "Fireball", characterClass: CharacterClass.WARRIOR)
            skillServiceHelper.createSkill(name: "Skill")
            def character = characterServiceHelper.createCharacter([characterClass: CharacterClass.MAGE])
        when:
            def response = httpGet(skillLearnUrl(skill1.id, character.id), SkillLite)
        then:
            response.status == HttpStatus.CONFLICT
            response.errorMessage == ErrorCodes.SKILL_CHARACTER_CLASS_MISMATCH.toString()
    }
}
