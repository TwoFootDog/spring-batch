package co.kr.kgc.point.batch.controller;

import co.kr.kgc.point.batch.common.util.quartz.ScheduleService;
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
            boolean result = scheduleService.startJob(jobName);
            if(!result) {
                return "fail";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
        return "success";
    }

    @GetMapping("/stop")
    public String stopJob(@RequestParam("jobExecutionId") long jobExecutionId) {
        try {
            boolean result = scheduleService.stopJob(jobExecutionId);
            if(!result) {
                return "fail";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
        return "success";
    }

    @GetMapping("/restart")
    public String restartJob(@RequestParam("jobExecutionId") long jobExecutionId) {
        try {
            boolean result = scheduleService.restartJob(jobExecutionId);
            if(!result) {
                return "fail";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
        return "success";
    }
}
