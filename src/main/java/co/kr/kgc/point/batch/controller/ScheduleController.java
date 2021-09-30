package co.kr.kgc.point.batch.controller;

import co.kr.kgc.point.batch.domain.ScheduleRequestDto;
import co.kr.kgc.point.batch.job.quartz.util.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/schedule")
@RequiredArgsConstructor
public class ScheduleController {
    /* 고민사항 :
     1. 동시실행방지
     2. 쿼츠 셋팅 확인
     3. 건수 셋팅
     4. 예외처리 */
    private final ScheduleService scheduleService;

    /* Job Schedule 신규 등록 */
    @PostMapping
    public String createJobSchedule(@RequestBody ScheduleRequestDto requestDto) {
        try {
            scheduleService.
                    createJobSchedule(requestDto);
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
        return "success";
    }

    /* Job Schedule 변경 */
    @PutMapping
    public String updateJobSchedule(@RequestBody ScheduleRequestDto requestDto,
                                    @RequestParam("jobName") String jobName,
                                    @RequestParam("jobGroup") String jobGroup
    ) {
        try {
            scheduleService.
                    updateJobSchedule(requestDto, jobName, jobGroup);
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
        return "success";
    }

    /* Job Schedule 삭제 */
    @DeleteMapping
    public String deleteJobSchedule(@RequestParam("jobName") String jobName,
                                    @RequestParam("jobGroup") String jobGroup) {
        try {
            scheduleService.deleteJobSchedule(jobName, jobGroup);
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
        return "success";
    }

    /* Job Schedule에 등록되어 있는 Job을 즉시 실행(Job Schedule에 미등록되어 있으면 실행 불가) */
    @GetMapping("/start")
    public String startJobSchedule(@RequestParam("jobName") String jobName,
                                   @RequestParam("jobGroup") String jobGroup) {
        try {
            scheduleService.startJobSchedule(jobName, jobGroup);
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
        return "success";
    }

    /* Job Schedule에 등록되어 있는 Job을 PAUSE 상태로 변경
    (Job Schedule에 미등록되어 있으면 실행 불가.
    Schedule이 실행중인 경우 완료 후 그 다음 Schedule부터 미실행) */
    @GetMapping("/stop")
    public String stopJobSchedule(@RequestParam("jobName") String jobName,
                                  @RequestParam("jobGroup") String jobGroup) {
        try {
            scheduleService.stopJobSchedule(jobName, jobGroup);
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
        return "success";
    }

    /* Job Schedule에 등록되어 있는 Job이 멈춰있을 때 재실행(Job Schedule에 미등록되어 있으면 실행 불가) */
    @GetMapping("/resume")
    public String resumeJobSchedule(@RequestParam("jobName") String jobName,
                                    @RequestParam("jobGroup") String jobGroup) {
        try {
            scheduleService.resumeJobSchdule(jobName, jobGroup);
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
        return "success";
    }
}
