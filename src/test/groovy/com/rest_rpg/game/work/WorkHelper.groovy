package com.rest_rpg.game.work

import com.rest_rpg.game.helpers.PageHelper
import com.rest_rpg.game.work.model.Work
import org.openapitools.model.WorkCreateRequest
import org.openapitools.model.WorkLite
import org.openapitools.model.WorkLitePage
import org.openapitools.model.WorkSearchRequest

class WorkHelper {

    static WorkCreateRequest createWorkCreateRequest(Map customArgs = [:]) {
        Map args = [
                name       : "Chop a tree",
                wage       : 50,
                workMinutes: 240
        ]

        args << customArgs

        return new WorkCreateRequest(args.name, args.wage, args.workMinutes)
    }

    static WorkSearchRequest createWorkSearchRequest(Map args = [:]) {
        new WorkSearchRequest()
                .pagination(PageHelper.createPagination(args))
                .nameLike(args.nameLike as String)
                .wageGreaterThanOrEqual(args.wageGreaterThanOrEqual as Integer)
                .wageLessThanOrEqual(args.wageLessThanOrEqual as Integer)
                .workMinutesGreaterThanOrEqual(args.workMinutesGreaterThanOrEqual as Integer)
                .workMinutesLessThanOrEqual(args.workMinutesLessThanOrEqual as Integer)
    }

    static boolean compare(WorkCreateRequest request, WorkLite lite) {
        assert request.name == lite.name
        assert request.wage == lite.wage
        assert request.workMinutes == lite.workMinutes

        true
    }

    static boolean compare(Work work, Work work2) {
        assert work.id == work2.id
        assert work.name == work2.name
        assert work.wage == work2.wage
        assert work.workMinutes == work2.workMinutes

        true
    }

    static boolean compare(Work work, WorkLite lite) {
        assert work.id == lite.id
        assert work.name == lite.name
        assert work.wage == lite.wage
        assert work.workMinutes == lite.workMinutes

        true
    }

    static boolean compare(List<Work> works, WorkLitePage page) {
        def workLites = page.content
        assert works.size() == workLites.size()
        works = works.sort { it.id }
        workLites = workLites.sort { it.id }
        assert works.withIndex().every { compare(it.v1, workLites[it.v2]) }

        true
    }
}
