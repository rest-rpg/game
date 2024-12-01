package com.rest_rpg.game.item

import com.rest_rpg.game.helpers.PageHelper
import com.rest_rpg.game.item.model.Item
import org.openapitools.model.ItemCreateRequest
import org.openapitools.model.ItemLite
import org.openapitools.model.ItemLitePage
import org.openapitools.model.ItemSearchRequest
import org.openapitools.model.ItemType

class ItemHelper {

    static ItemCreateRequest createRequest(Map customArgs = [:]) {
        Map args = [
                name : "Sword",
                type : ItemType.WEAPON,
                power: 20,
                price: 50
        ]
        args << customArgs

        new ItemCreateRequest(args.name, args.type, args.power, args.price)
    }

    static ItemSearchRequest createItemSearchRequest(Map args = [:]) {
        new ItemSearchRequest()
                .pagination(PageHelper.createPagination(args))
                .nameLike(args.nameLike as String)
                .typeIn(args.typeIn as List)
                .powerGreaterThanOrEqual(args.powerGreaterThanOrEqual as Integer)
                .powerLessThanOrEqual(args.powerLessThanOrEqual as Integer)
                .priceGreaterThanOrEqual(args.priceGreaterThanOrEqual as Integer)
                .priceLessThanOrEqual(args.priceLessThanOrEqual as Integer)
    }

    static boolean compare(ItemCreateRequest request, ItemLite lite) {
        assert request.name == lite.name
        assert request.type.toString() == lite.type
        assert request.power == lite.power
        assert request.price == lite.price

        true
    }

    static boolean compare(Item item, ItemLite lite) {
        assert item.id == lite.id
        assert item.name == lite.name
        assert item.type.toString() == lite.type
        assert item.power == lite.power
        assert item.price == lite.price

        true
    }

    static boolean compare(Item item1, Item item2) {
        assert item1.id == item2.id
        assert item1.name == item2.name
        assert item1.type == item2.type
        assert item1.power == item2.power
        assert item1.price == item2.price

        true
    }

    static boolean compare(List<Item> items, ItemLitePage page) {
        def itemLiteList = page.content
        assert items.size() == itemLiteList.size()
        items = items.sort { it.id }
        itemLiteList = itemLiteList.sort { it.id }
        assert items.withIndex().every { compare(it.v1, itemLiteList[it.v2]) }

        true
    }
}
