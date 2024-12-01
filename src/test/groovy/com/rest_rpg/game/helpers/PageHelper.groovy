package com.rest_rpg.game.helpers

import org.openapitools.model.Pagination

class PageHelper {

    static Pagination createPagination(Map customArgs = [:]) {
        Map args = [
                pageNumber: 0,
                elements  : 10,
                sort      : "id",
                sortOrder : "ASC"
        ]

        args << customArgs
        new Pagination()
                .pageNumber(args.pageNumber)
                .elements(args.elements)
                .sort(args.sort)
                .sortOrder(args.sortOrder)
    }
}
