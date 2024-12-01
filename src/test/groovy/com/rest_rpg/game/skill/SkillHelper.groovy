package com.rest_rpg.game.skill

import com.rest_rpg.game.helpers.PageHelper
import com.rest_rpg.game.skill.model.Skill
import org.openapitools.model.CharacterClass
import org.openapitools.model.CharacterSkillBasic
import org.openapitools.model.CharacterSkillBasics
import org.openapitools.model.SkillBasic
import org.openapitools.model.SkillBasicPage
import org.openapitools.model.SkillCreateRequest
import org.openapitools.model.SkillDetails
import org.openapitools.model.SkillEffect
import org.openapitools.model.SkillLite
import org.openapitools.model.SkillLites
import org.openapitools.model.SkillSearchRequest
import org.openapitools.model.SkillType

class SkillHelper {

    static SkillCreateRequest createSkillCreateRequest(Map customArgs = [:]) {
        Map args = [
                name                    : "Bash",
                manaCost                : 10,
                type                    : SkillType.NORMAL_DAMAGE.toString(),
                multiplier              : 1.2f,
                multiplierPerLevel      : 0.1f,
                effect                  : SkillEffect.STUNNED.toString(),
                effectDuration          : 2,
                effectDurationPerLevel  : 1,
                effectMultiplier        : 0,
                effectMultiplierPerLevel: 0,
                goldCost                : 50,
                statisticPointsCost     : 5,
                characterClass          : CharacterClass.WARRIOR.toString()
        ]

        args << customArgs
        def request = new SkillCreateRequest(args.name,
                args.manaCost,
                args.type,
                args.multiplier,
                args.multiplierPerLevel,
                args.effectDuration,
                args.effectDurationPerLevel,
                args.effectMultiplier,
                args.effectMultiplierPerLevel,
                args.goldCost,
                args.statisticPointsCost,
                args.characterClass)
        request.effect(args.effect)
        request.effectMultiplier(args.effectMultiplier)
        return request
    }

    static SkillSearchRequest createSkillSearchRequest(Map args = [:]) {
        new SkillSearchRequest()
                .pagination(PageHelper.createPagination(args))
                .nameLike(args.name as String)
                .skillTypeIn(args.skillTypeIn as List)
                .skillEffectIn(args.skillEffectIn as List)
                .characterClassIn(args.characterClassIn as List)
    }

    static boolean compare(SkillCreateRequest request, SkillLite skillLite) {
        assert request.name == skillLite.name

        true
    }

    static boolean compare(Skill skill, SkillLite skillLite) {
        assert skill.id == skillLite.id
        assert skill.name == skillLite.name

        true
    }

    static boolean compare(List<Skill> skills, SkillLites skillLites) {
        def skillLiteList = skillLites.content
        assert skills.size() == skillLiteList.size()
        skills = skills.sort { it.id }
        skillLiteList = skillLiteList.sort { it.id }
        assert skills.withIndex().every { compare(it.v1, skillLiteList[it.v2]) }

        true
    }

    static boolean compare(Skill skill, SkillBasic skillBasic) {
        assert skill.id == skillBasic.id
        assert skill.name == skillBasic.name
        assert skill.type.toString() == skillBasic.type
        assert skill.effect.toString() == skillBasic.effect
        assert skill.characterClass.toString() == skillBasic.characterClass
        assert skill.skillTraining.goldCost == skillBasic.skillTraining.goldCost
        assert skill.skillTraining.statisticPointsCost == skillBasic.skillTraining.statisticPointsCost

        true
    }

    static boolean compare(List<Skill> skills, SkillBasicPage skillBasicPage) {
        def skillBasics = skillBasicPage.content
        assert skills.size() == skillBasics.size()
        skills = skills.sort { it.id }
        skillBasics = skillBasics.sort { it.id }
        assert skills.withIndex().every { compare(it.v1, skillBasics[it.v2]) }

        true
    }

    static boolean compare(Skill skill, CharacterSkillBasic basic) {
        assert compare(skill, basic)

        true
    }

    static boolean compare(List<Skill> skills, CharacterSkillBasics basics) {
        def basicsList = basics.content.skill
        assert skills.size() == basicsList.size()
        skills = skills.sort { it.id }
        basicsList = basicsList.sort { it.id }
        assert skills.withIndex().every { compare(it.v1, basicsList[it.v2]) }

        true
    }

    static boolean compare(Skill skill, SkillDetails skillDetails) {
        assert skill.id == skillDetails.id
        assert skill.name == skillDetails.name
        assert skill.manaCost == skillDetails.manaCost
        assert skill.type.toString() == skillDetails.type
        assert skill.effect.toString() == skillDetails.effect
        assert skill.characterClass.toString() == skillDetails.characterClass
        assert skill.multiplier == skillDetails.multiplier
        assert skill.multiplierPerLevel == skillDetails.multiplierPerLevel
        assert skill.effectDuration == skillDetails.effectDuration
        assert skill.effectMultiplier == skillDetails.effectMultiplier
        assert skill.effectDurationPerLevel == skillDetails.effectDurationPerLevel
        assert skill.effectMultiplierPerLevel == skillDetails.effectMultiplierPerLevel

        true
    }

    static boolean compare(Collection<Skill> skills, List<SkillDetails> details) {
        assert skills.size() == details.size()
        skills = skills.sort { it.id }
        details = details.sort { it.id }
        assert skills.withIndex().every { compare(it.v1, details[it.v2]) }

        true
    }

    static boolean compare(Skill skill1, Skill skill2) {
        assert skill1.id == skill2.id
        assert skill1.name == skill2.name
        assert skill1.manaCost == skill2.manaCost
        assert skill1.type == skill2.type
        assert skill1.effect == skill2.effect
        assert skill1.characterClass == skill2.characterClass
        assert skill1.multiplier == skill2.multiplier
        assert skill1.multiplierPerLevel == skill2.multiplierPerLevel
        assert skill1.effectDuration == skill2.effectDuration
        assert skill1.effectMultiplier == skill2.effectMultiplier
        assert skill1.effectDurationPerLevel == skill2.effectDurationPerLevel
        assert skill1.effectMultiplierPerLevel == skill2.effectMultiplierPerLevel

        true
    }
}
