/*
 * @file : com.project.batch.domain.common.service.BatchService.java
 * @desc : BatchController에서 직접 호출해주는 Spring Batch 관련 서비스가 명시된 클래스(배치 시작/종료 처리)
 * @auth :
 * @version : 1.0
 * @history
 * version (tag)     프로젝트명     일자      성명    변경내용
 * -------------    ----------   ------   ------  --------
 *
 * */

package com.project.batch.domain.common.service;

import com.project.batch.domain.common.dto.BatchResponseDto;

public interface BatchService {

    /*
     * @method : startJob
     * @desc : Batch Job을 즉시 시작시키는 메소드(Quartz 스케쥴에 미등록되어 있어도 실행 가능함)
     * @param : jobName(배치Job명), args1, args2, args3
     * @return : BatchResponseDto
     * */
    public BatchResponseDto startJob(String jobName, String args1, String args2, String args3);

    /*
     * @method : stopJob
     * @desc : Batch Job을 즉시 중지시키는 메소드(Quartz 스케쥴에 미등록되어 있어도 실행 가능함)
     *         배치 Job 상태를 STOPPED로 변경하며, 동일한 배치JOB을 다시 실행시키면, 그 전 실행이력과는 관계없이 재 실행됨(신규 ROW 생성)
     *         배치 JOB 내에서 트랜잭션이 다른 서비스는 트랜잭션 처리가 완료될 때까지 중지되지 않고(STOPPING 상태)
     *         처리 완료 후 중지 처리된다(상태가 STOPPED로 변경됨)
     * @param : jobExecutionId(배치 실행ID)
     * @return : BatchResponseDto
     * */
    public BatchResponseDto stopJob(long jobExecutionId);
}