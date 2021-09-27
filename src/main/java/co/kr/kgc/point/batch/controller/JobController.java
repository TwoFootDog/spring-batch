package co.kr.kgc.point.batch.controller;

import co.kr.kgc.point.batch.job.Quartz.SchedulerJobInfo;
import co.kr.kgc.point.batch.job.Quartz.SchedulerJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class JobController {

    private final SchedulerJobService schedulerJobService;

    @PostMapping("/batch")
    public String createJob(@RequestBody SchedulerJobInfo schedulerJobInfo) {
        try {
            schedulerJobService.saveOrUpdate(schedulerJobInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "success";
    }
}
