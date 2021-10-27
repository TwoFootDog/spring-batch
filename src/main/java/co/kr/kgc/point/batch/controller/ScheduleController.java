package co.kr.kgc.point.batch.controller;

import co.kr.kgc.point.batch.common.util.quartz.ScheduleService;
import co.kr.kgc.point.batch.domain.ScheduleRequestDto;
import co.kr.kgc.point.batch.domain.ScheduleResponseDto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {
    /* 고민사항 :
     1. 동시실행방지
     2. 쿼츠 셋팅 확인 - 확인 거의 완료
     3. 건수 셋팅 - tasklet 확인 필요
     4. 예외처리 */

    private final ScheduleService scheduleService;
    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    /* Job Schedule 신규 등록 */
    @PostMapping
    public ScheduleResponseDto createJobSchedule(@RequestBody ScheduleRequestDto requestDto) {
        return scheduleService.createJobSchedule(requestDto);
    }

    /* Job Schedule 변경 */
    @PutMapping
    public ScheduleResponseDto updateJobSchedule(@RequestBody ScheduleRequestDto requestDto,
                                    @RequestParam("jobName") String jobName,
                                    @RequestParam("jobGroup") String jobGroup) {
        return scheduleService.updateJobSchedule(requestDto, jobName, jobGroup);
    }

    /* Job Schedule 삭제 */
    @DeleteMapping
    public ScheduleResponseDto deleteJobSchedule(@RequestParam("jobName") String jobName,
                                    @RequestParam("jobGroup") String jobGroup) {
        return scheduleService.deleteJobSchedule(jobName, jobGroup);
    }

    /* Job Schedule에 등록되어 있는 Job을 즉시 실행(Job Schedule에 미등록되어 있으면 실행 불가) */
    @GetMapping("/start")
    public ScheduleResponseDto startJobSchedule(@RequestParam("jobName") String jobName,
                                   @RequestParam("jobGroup") String jobGroup) {
        return scheduleService.startJobSchedule(jobName, jobGroup);

    }

    /* Job Schedule에 등록되어 있는 Job을 PAUSE 상태로 변경
    (Job Schedule에 미등록되어 있으면 실행 불가.
    Schedule이 실행중인 경우 완료 후 그 다음 Schedule부터 미실행) */
    @GetMapping("/stop")
    public ScheduleResponseDto stopJobSchedule(@RequestParam("jobName") String jobName,
                                  @RequestParam("jobGroup") String jobGroup) {
        return scheduleService.stopJobSchedule(jobName, jobGroup);
    }

    /* Job Schedule에 등록되어 있는 Job이 멈춰있을 때 재실행(Job Schedule에 미등록되어 있으면 실행 불가) */
    @GetMapping("/resume")
    public ScheduleResponseDto resumeJobSchedule(@RequestParam("jobName") String jobName,
                                    @RequestParam("jobGroup") String jobGroup) {
        return scheduleService.resumeJobSchdule(jobName, jobGroup);
    }
}
