package co.kr.kgc.point.batch.controller;

import co.kr.kgc.point.batch.domain.SchedulerRequestDto;
import co.kr.kgc.point.batch.job.quartz.util.SchedulerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class JobController {

    private final SchedulerService schedulerJobService;

    @PostMapping("/scheduler")
    public String createJobSchedule(@RequestBody SchedulerRequestDto requestDto) {
        try {
            schedulerJobService.
                    createJobSchedule(requestDto);
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
        return "success";
    }

    @PutMapping("/scheduler")
    public String updateJobSchedule(@RequestBody SchedulerRequestDto requestDto,
                                    @RequestParam("jobGroup") String jobGroup,
                                    @RequestParam("jobName") String jobName) {
        try {
            schedulerJobService.
                    updateJobSchedule(requestDto, jobGroup, jobName);
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
        return "success";
    }

    @DeleteMapping("/scheduler")
    public String deleteJobSchedule(@RequestParam("jobGroup") String jobGroup,
                                    @RequestParam("jobName") String jobName) {
        try {
            schedulerJobService.deleteJobSchedule(jobGroup, jobName);
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
        return "success";
    }

    @GetMapping
    public String startJob(@RequestParam("jobGroup") String jobGroup,
                           @RequestParam("jobName") String jobName) {
        try {
            schedulerJobService.startJob(jobGroup, jobName);
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
        return "success";
    }
}
