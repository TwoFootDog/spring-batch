package co.kr.kgc.point.batch.controller;

import co.kr.kgc.point.batch.job.quartz.domain.SchedulerJobDto;
import co.kr.kgc.point.batch.job.quartz.util.SchedulerJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class JobController {

    private final SchedulerJobService schedulerJobService;

    @PostMapping("/scheduler")
    public String createJob(@RequestBody SchedulerJobDto schedulerJobDto) {
        try {
            schedulerJobService.saveOrUpdate(schedulerJobDto);
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
        return "success";
    }

//    @GetMapping
}
