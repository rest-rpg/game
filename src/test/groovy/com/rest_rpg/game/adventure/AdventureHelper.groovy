package com.rest_rpg.game.adventure

import com.rest_rpg.game.adventure.model.Adventure
import com.rest_rpg.game.enemy.EnemyHelper
import com.rest_rpg.game.enemy.model.Enemy
import com.rest_rpg.game.helpers.PageHelper
import org.openapitools.model.AdventureBasic
import org.openapitools.model.AdventureBasicPage
import org.openapitools.model.AdventureCreateRequest
import org.openapitools.model.AdventureDetails
import org.openapitools.model.AdventureLite
import org.openapitools.model.AdventureSearchRequest

class AdventureHelper {

    static AdventureCreateRequest createAdventureCreateRequest(Enemy enemy, Map customArgs = [:]) {
        Map args = [
                name                    : "Kill bear",
                adventureLengthInMinutes: 90,
                xpForAdventure          : 100,
                goldForAdventure        : 110
        ]

        args << customArgs

        return new AdventureCreateRequest(args.name, args.adventureLengthInMinutes, args.xpForAdventure, args.goldForAdventure, enemy.id)
    }

    static AdventureSearchRequest createAdventureSearchRequest(Map args = [:]) {
        new AdventureSearchRequest()
                .pagination(PageHelper.createPagination(args))
                .nameLike(args.nameLike as String)
                .adventureTimeGreaterThanOrEqual(args.adventureTimeGreaterThanOrEqual as Integer)
                .adventureTimeLessThanOrEqual(args.adventureTimeLessThanOrEqual as Integer)
                .xpGreaterThanOrEqual(args.xpGreaterThanOrEqual as Integer)
                .xpLessThanOrEqual(args.xpLessThanOrEqual as Integer)
                .goldGreaterThanOrEqual(args.goldGreaterThanOrEqual as Integer)
                .goldLessThanOrEqual(args.goldLessThanOrEqual as Integer)
                .enemyNameLike(args.enemyNameLike as String)
    }

    static boolean compare(AdventureCreateRequest request, AdventureLite lite) {
        assert request.name == lite.name

        true
    }

    static boolean compare(Adventure adventure, AdventureLite lite) {
        assert adventure.id == lite.id
        assert adventure.name == lite.name

        true
    }

    static boolean compare(Adventure adventure, AdventureBasic basic) {
        assert adventure.id == basic.id
        assert adventure.name == basic.name
        assert adventure.adventureTimeInMinutes == basic.adventureTimeInMinutes
        assert adventure.xpForAdventure == basic.xpForAdventure
        assert adventure.goldForAdventure == basic.goldForAdventure
        assert EnemyHelper.compare(adventure.enemy, basic.enemy)

        true
    }

    static boolean compare(Adventure adventure1, Adventure adventure2) {
        assert adventure1.id == adventure2.id
        assert adventure1.name == adventure2.name
        assert adventure1.adventureTimeInMinutes == adventure2.adventureTimeInMinutes
        assert adventure1.xpForAdventure == adventure2.xpForAdventure
        assert adventure1.goldForAdventure == adventure2.goldForAdventure
        assert EnemyHelper.compare(adventure1.enemy, adventure2.enemy)

        true
    }

    static boolean compare(Adventure adventure, AdventureDetails details) {
        assert adventure.id == details.id
        assert adventure.name == details.name
        assert adventure.adventureTimeInMinutes == details.adventureTimeInMinutes
        assert adventure.xpForAdventure == details.xpForAdventure
        assert adventure.goldForAdventure == details.goldForAdventure
        assert EnemyHelper.compare(adventure.enemy, details.enemy)

        true
    }

    static boolean compare(List<Adventure> adventures, AdventureBasicPage page) {
        def adventureBasicList = page.content
        assert adventures.size() == adventureBasicList.size()
        adventures = adventures.sort { it.id }
        adventureBasicList = adventureBasicList.sort { it.id }
        assert adventures.withIndex().every { compare(it.v1, adventureBasicList[it.v2]) }

        true
    }
}
