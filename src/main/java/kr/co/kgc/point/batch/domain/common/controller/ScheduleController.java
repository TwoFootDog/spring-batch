/*
 * @file : kr.co.kgc.point.batch.domain.common.controller.ScheduleController.java
 * @desc : Quartz Schedule 등록/수정/삭제/시작/중지/재시작 서비스를 호출하는 Controller
 * @auth :
 * @version : 1.0
 * @history
 * version (tag)     프로젝트명     일자      성명    변경내용
 * -------------    ----------   ------   ------  --------
 *
 * */

package kr.co.kgc.point.batch.domain.common.controller;

import kr.co.kgc.point.batch.domain.common.service.ScheduleService;
import kr.co.kgc.point.batch.domain.common.dto.ScheduleRequestDto;
import kr.co.kgc.point.batch.domain.common.dto.ScheduleResponseDto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    /*
     * @method : createJobSchedule
     * @desc : Quartz Schedule 신규 등록
     * @param : ScheduleRequestDto (jobName/jobGroup(스케쥴 Job 이름/그룹(QUARTZ_JOB_DETAILS 테이블 참고.
     *                              Job이름은 Spring Batch Job의 이름과 동일해야 함)),
     *                              startTime(스케쥴 시작 시간), cronExpression(스케쥴 표현식), desc(상세설명))
     * @return : ScheduleResponseDto ((resultCode/resultMessage(응답코드/메시지(messageCode.yml 참고),
     *                                그 외 항목은 ScheduleRequestDto 참고)
     * */
    @PostMapping
    public ScheduleResponseDto createJobSchedule(@RequestBody ScheduleRequestDto requestDto) {
        return scheduleService.createJobSchedule(requestDto);
    }

    /*
     * @method : updateJobSchedule
     * @desc : Quartz Schedule 변경
     * @param : jobName/jobGroup(스케쥴 Job 이름/그룹(QUARTZ_JOB_DETAILS 테이블 참고)),
     *          ScheduleRequestDto (startTime(스케쥴 시작 시간), cronExpression(스케쥴 표현식))
     * @return : ScheduleResponseDto ((resultCode/resultMessage(응답코드/메시지(messageCode.yml 참고),
     *                                그 외 항목은 ScheduleRequestDto 참고)
     * */
    @PutMapping
    public ScheduleResponseDto updateJobSchedule(@RequestBody ScheduleRequestDto requestDto,
                                    @RequestParam("jobName") String jobName,
                                    @RequestParam("jobGroup") String jobGroup) {
        return scheduleService.updateJobSchedule(requestDto, jobName, jobGroup);
    }

    /*
     * @method : deleteJobSchedule
     * @desc : Quartz Schedule 삭제
     * @param : jobName/jobGroup(스케쥴 Job 이름/그룹(QUARTZ_JOB_DETAILS 테이블 참고)),
     * @return : ScheduleResponseDto ((resultCode/resultMessage(응답코드/메시지(messageCode.yml 참고),
     *                                그 외 항목은 ScheduleRequestDto 참고)
     * */
    @DeleteMapping
    public ScheduleResponseDto deleteJobSchedule(@RequestParam("jobName") String jobName,
                                    @RequestParam("jobGroup") String jobGroup) {
        return scheduleService.deleteJobSchedule(jobName, jobGroup);
    }

    /*
     * @method : startJobSchedule
     * @desc : Quartz Schedule에 등록되어 있는 Job을 즉시 실행(Quartz Schedule에 미등록되어 있으면 실행 불가)
     * @param : jobName/jobGroup(스케쥴 Job 이름/그룹(QUARTZ_JOB_DETAILS 테이블 참고)),
     * @return : ScheduleResponseDto ((resultCode/resultMessage(응답코드/메시지(messageCode.yml 참고),
     *                                그 외 항목은 ScheduleRequestDto 참고)
     * */
    @GetMapping("/start")
    public ScheduleResponseDto startJobSchedule(@RequestParam("jobName") String jobName,
                                   @RequestParam("jobGroup") String jobGroup) {
        return scheduleService.startJobSchedule(jobName, jobGroup);

    }

    /*
     * @method : stopJobSchedule
     * @desc : Quartz Schedule의 상태를 중지 상태로 변경(QUARTZ_TRIGGERS의 TRIGGER_STATE를 PAUSED로 변경)
               Schedule이 실행중인 경우 완료 후 그 다음 Schedule부터 미실행
     * @param : jobName/jobGroup(스케쥴 Job 이름/그룹(QUARTZ_JOB_DETAILS 테이블 참고)),
     * @return : ScheduleResponseDto ((resultCode/resultMessage(응답코드/메시지(messageCode.yml 참고),
     *                                그 외 항목은 ScheduleRequestDto 참고)
     * */
    @GetMapping("/stop")
    public ScheduleResponseDto stopJobSchedule(@RequestParam("jobName") String jobName,
                                  @RequestParam("jobGroup") String jobGroup) {
        return scheduleService.stopJobSchedule(jobName, jobGroup);
    }

    /*
     * @method : resumeJobSchedule
     * @desc : Quartz Schedule이 중지 상태인 경우(TRIGGER_STATE가 PAUSED) 실행 가능한 상태로 변경
     * @param : jobName/jobGroup(스케쥴 Job 이름/그룹(QUARTZ_JOB_DETAILS 테이블 참고)),
     * @return : ScheduleResponseDto ((resultCode/resultMessage(응답코드/메시지(messageCode.yml 참고),
     *                                그 외 항목은 ScheduleRequestDto 참고)
     * */
    @GetMapping("/resume")
    public ScheduleResponseDto resumeJobSchedule(@RequestParam("jobName") String jobName,
                                    @RequestParam("jobGroup") String jobGroup) {
        return scheduleService.resumeJobSchdule(jobName, jobGroup);
    }
}
