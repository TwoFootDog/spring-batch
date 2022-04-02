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

import com.project.batch.common.exception.BatchRequestException;
import com.project.batch.domain.common.dto.BatchResponseDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.*;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;

@Service
public class BatchService {

    private static final Logger log = LogManager.getLogger();

    private final JobLauncher jobLauncher;
    private final JobLocator jobLocator;
    private final JobOperator jobOperator;
    private final JobExplorer jobExplorer;
    private final MessageSource messageSource;

    public BatchService(JobLauncher jobLauncher, JobLocator jobLocator, JobOperator jobOperator,
                        JobExplorer jobExplorer, MessageSource messageSource) {
        this.jobLauncher = jobLauncher;
        this.jobLocator = jobLocator;
        this.jobOperator = jobOperator;
        this.jobExplorer = jobExplorer;
        this.messageSource = messageSource;
    }

    /*
     * @method : startJob
     * @desc : Batch Job을 즉시 시작시키는 메소드(Quartz 스케쥴에 미등록되어 있어도 실행 가능함)
     * @param : jobName(배치Job명)
     * @return : BatchResponseDto
     * */
    public BatchResponseDto startJob(String jobName) {
        String requestDate = new SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis());
        JobParameters jobParameters = new JobParametersBuilder()
                                            .addString("--job.name", jobName)
                                            .addString("requestDate", requestDate)
                                            .toJobParameters();
        try {
            /* 동일한 Job 명을 가진 배치가 실행 중인 경우 예외 처리 */
            if (jobExplorer.findRunningJobExecutions(jobName).size() >= 1) {
                log.error(">> Job is already running : {}", jobName);
                throw new BatchRequestException("batch.response.fail", "Job is already running: "+ jobName);
            }

            /* Batch Job 실행 */
            Job job = jobLocator.getJob(jobName);
            JobExecution jobExecution = jobLauncher.run(job, jobParameters);
            log.info(">> job start success. jobExecutionId : [" + jobExecution.getId() + "]");
            return new BatchResponseDto
                    .Builder()
                    .jobName(jobName)
                    .jobExecutionId(jobExecution.getId())
                    .requestDate(requestDate)
                    .resultCodeMsg("batch.response.success")
                    .build();
        } catch (NoSuchJobException | JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
            log.error("Failed to start Job. jobName :  {}, message : {}", jobName, e.getMessage());
            throw new BatchRequestException("batch.response.fail", e.getMessage());
        }
    }

    /*
     * @method : stopJob
     * @desc : Batch Job을 즉시 중지시키는 메소드(Quartz 스케쥴에 미등록되어 있어도 실행 가능함)
     *         배치 Job 상태를 STOPPED로 변경하며, 동일한 배치JOB을 다시 실행시키면, 그 전 실행이력과는 관계없이 재 실행됨(신규 ROW 생성)
     *         배치 JOB 내에서 트랜잭션이 다른 서비스는 트랜잭션 처리가 완료될 때까지 중지되지 않고(STOPPING 상태)
     *         처리 완료 후 중지 처리된다(상태가 STOPPED로 변경됨)
     * @param : jobExecutionId(배치 실행ID)
     * @return : BatchResponseDto
     * */
    public BatchResponseDto stopJob(long jobExecutionId) {
        String requestDate = new SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis());
        JobExecution jobExecution = jobExplorer.getJobExecution(jobExecutionId);
        String jobName = jobExecution != null ? jobExecution.getJobInstance().getJobName() : "";

        try {
            boolean result = jobOperator.stop(jobExecutionId);
            if (result) {
                log.info(">> job stop success. jobExecutionId : [" + jobExecutionId + "]");
                return new BatchResponseDto
                        .Builder()
                        .jobName(jobName)
                        .jobExecutionId(jobExecutionId)
                        .requestDate(requestDate)
                        .resultCodeMsg("batch.response.success")
                        .build();
            } else {
                log.error(">> Failed to stop Job. jobName :  {}", jobName);
                throw new BatchRequestException("batch.response.fail", "Failed to stop job. stop method result false");
            }
        } catch (NoSuchJobExecutionException | JobExecutionNotRunningException e) {
            log.error("Failed to stop Job. jobName :  {}, message : {}", jobName, e.getMessage());
            throw new BatchRequestException("batch.response.fail", e.getMessage());
        }
    }
}