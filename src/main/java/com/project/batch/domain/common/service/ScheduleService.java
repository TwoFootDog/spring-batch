/*
 * @file : com.project.batch.domain.common.service.ScheduleService.java
 * @desc : ScheduleController에서 직접 호출해주는 Quartz Schedule 관련 서비스가 명시된 클래스
 *         (스케쥴러 등록/수정/삭제 및 스케쥴러 시작/중지 처리)
 * @auth :
 * @version : 1.0
 * @history
 * version (tag)     프로젝트명     일자      성명    변경내용
 * -------------    ----------   ------   ------  --------
 *
 * */

package com.project.batch.domain.common.service;

import com.project.batch.domain.common.dto.ScheduleRequestDto;
import com.project.batch.domain.common.dto.ScheduleResponseDto;

public interface ScheduleService {
    /*
     * @method : createJobSchedule
     * @desc : Quartz Schedule 신규 등록
     * @param : ScheduleRequestDto (jobName/jobGroup(스케쥴 Job 이름/그룹(QUARTZ_JOB_DETAILS 테이블 참고.
     *                              Job이름은 Spring Batch Job의 이름과 동일해야 함)),
     *                              startTime(스케쥴 시작 시간), cronExpression(스케쥴 표현식), desc(상세설명))
     * @return : ScheduleResponseDto ((resultCode/resultMessage(응답코드/메시지(messageCode.yml 참고),
     *                                그 외 항목은 ScheduleRequestDto 참고)
     * */
    public ScheduleResponseDto createJobSchedule(ScheduleRequestDto requestDto);

    /*
     * @method : updateJobSchedule
     * @desc : Quartz Schedule 변경
     * @param : jobName/jobGroup(스케쥴 Job 이름/그룹(QUARTZ_JOB_DETAILS 테이블 참고)),
     *          ScheduleRequestDto (startTime(스케쥴 시작 시간), cronExpression(스케쥴 표현식))
     * @return : ScheduleResponseDto ((resultCode/resultMessage(응답코드/메시지(messageCode.yml 참고),
     *                                그 외 항목은 ScheduleRequestDto 참고)
     * */
    public ScheduleResponseDto updateJobSchedule(ScheduleRequestDto requestDto, String jobName, String jobGroup);

    /*
     * @method : deleteJobSchedule
     * @desc : Quartz Schedule 삭제
     * @param : jobName/jobGroup(스케쥴 Job 이름/그룹(QUARTZ_JOB_DETAILS 테이블 참고)),
     * @return : ScheduleResponseDto ((resultCode/resultMessage(응답코드/메시지(messageCode.yml 참고),
     *                                그 외 항목은 ScheduleRequestDto 참고)
     * */
    public ScheduleResponseDto deleteJobSchedule(String jobName, String jobGroup);

    /*
     * @method : startJobSchedule
     * @desc : Quartz Schedule에 등록되어 있는 Job을 즉시 실행(Quartz Schedule에 미등록되어 있으면 실행 불가)
     * @param : jobName/jobGroup(스케쥴 Job 이름/그룹(QUARTZ_JOB_DETAILS 테이블 참고)),
     * @return : ScheduleResponseDto ((resultCode/resultMessage(응답코드/메시지(messageCode.yml 참고),
     *                                그 외 항목은 ScheduleRequestDto 참고)
     * */
    public ScheduleResponseDto startJobSchedule(String jobName, String jobGroup);
    /*
     * @method : stopJobSchedule
     * @desc : Quartz Schedule의 상태를 중지 상태로 변경(QUARTZ_TRIGGERS의 TRIGGER_STATE를 PAUSED로 변경)
               Schedule이 실행중인 경우 완료 후 그 다음 Schedule부터 미실행
     * @param : jobName/jobGroup(스케쥴 Job 이름/그룹(QUARTZ_JOB_DETAILS 테이블 참고)),
     * @return : ScheduleResponseDto ((resultCode/resultMessage(응답코드/메시지(messageCode.yml 참고),
     *                                그 외 항목은 ScheduleRequestDto 참고)
     * */
    public ScheduleResponseDto stopJobSchedule(String jobName, String jobGroup);
    /*
     * @method : resumeJobSchedule
     * @desc : Quartz Schedule이 중지 상태인 경우(TRIGGER_STATE가 PAUSED) 실행 가능한 상태로 변경
     * @param : jobName/jobGroup(스케쥴 Job 이름/그룹(QUARTZ_JOB_DETAILS 테이블 참고)),
     * @return : ScheduleResponseDto ((resultCode/resultMessage(응답코드/메시지(messageCode.yml 참고),
     *                                그 외 항목은 ScheduleRequestDto 참고)
     * */
    public ScheduleResponseDto resumeJobSchdule(String jobName, String jobGroup);
}