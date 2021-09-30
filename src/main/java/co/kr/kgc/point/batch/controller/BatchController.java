package co.kr.kgc.point.batch.controller;

import co.kr.kgc.point.batch.job.quartz.util.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/batch")
@RequiredArgsConstructor
public class BatchController {

    private final ScheduleService scheduleService;

    /* Batch Job 즉시 실행(Job Schedule에 미등록되어 있어도 실행 가능) */
    @GetMapping("/start")
    public String startJob(@RequestParam("jobName") String jobName) {
        try {
            scheduleService.startJob(jobName);
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
        return "success";
    }
}
