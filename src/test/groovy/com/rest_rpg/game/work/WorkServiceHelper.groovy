package com.rest_rpg.game.work

import com.rest_rpg.game.work.model.Work
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class WorkServiceHelper {

    @Autowired
    WorkRepository workRepository;

    def clean() {
        workRepository.deleteAll()
    }

    Work saveWork(Map customArgs = [:]) {
        Map args = [
                name       : "Kill bear",
                wage       : 90,
                workMinutes: 100,
                deleted    : false
        ]
        args << customArgs

        def work = Work.builder()
                .name(args.name)
                .wage(args.wage)
                .workMinutes(args.workMinutes)
                .deleted(args.deleted)
                .build()

        workRepository.save(work)
    }

    Work getWork(long workId) {
        workRepository.findById(workId).orElseThrow()
    }
}
