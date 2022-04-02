/*
 * @file : com.project.batch.domain.sample.listener.SampleJobListener.java
 * @desc : Sample Job 메인 로직 실행 전/후 처리를 수행하는 리스너
 * @auth :
 * @version : 1.0
 * @history
 * version (tag)     프로젝트명     일자      성명    변경내용
 * -------------    ----------   ------   ------  --------
 *
 * */

package com.project.batch.domain.sample.listener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

@Component
public class SampleJobListener implements JobExecutionListener {

    private static final Logger log = LogManager.getLogger();
    private final JobExplorer jobExplorer;
    private final MessageSource messageSource;

    public SampleJobListener(JobExplorer jobExplorer, MessageSource messageSource) {
        this.jobExplorer = jobExplorer;
        this.messageSource = messageSource;
    }

    /*
     * @method : beforeJob
     * @desc : Sample Job 메인 로직 시작 전 실행. 동일한 Job이 실행 중이면 예외처리(배치 미실행)
     * @param :
     * @return :
     * */
    @Override
    public void beforeJob(JobExecution jobExecution) {
        String jobName = jobExecution.getJobInstance().getJobName();
        long jobExecutionId = jobExecution.getId();
        String startTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(jobExecution.getStartTime());

        /* 동일한 실행 중인 경우 예외 처리 */
         if (jobExplorer.findRunningJobExecutions(jobName).size() > 1) {
             log.error(">> [" + jobExecutionId + "] " + " Job is already running");
            throw new RuntimeException("Job is already running: "+ jobExecution.getJobInstance().getJobName());
         }

        log.info(">> [" + jobExecutionId + "] "
                + "batch Job Start. "
                + "jobName : [" + jobName + "]. "
                + "jobExecutionId : ["  + jobExecutionId + "]. "
                + "startTime : [" + startTime + "]" );
    }

    /*
     * @method : afterJob
     * @desc : Sample Job 완료 전 실행. Batch Job의 상태 등을 저장 후 로깅
     * @param :
     * @return :
     * */
    @Override
    public void afterJob(JobExecution jobExecution) {
        String jobName = jobExecution.getJobInstance().getJobName();
        long jobExecutionId = jobExecution.getId();
        String startTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(jobExecution.getStartTime());
        String endTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(jobExecution.getEndTime());
        String exitCode = jobExecution.getExitStatus().getExitCode();
        String exitMessage = null;
        log.info(">> [" + jobExecutionId + "] "
                + "batch Job End. "
                + "jobName : [" + jobName + "]. "
                + "jobExecutionId : ["  + jobExecutionId + "]. "
                + "startTime : [" + startTime + "]. "
                + "endTime : [" + endTime + "]. "
                + "exitCode : [" + exitCode + "]");

        /* exit message resultMessage */
        if ("COMPLETED".equals(exitCode)) {
            exitMessage = messageSource.getMessage("BATCH_JOB_STATUS_COMPLETED", new String[]{}, null);
        } else if ("STOPPED".equals(exitCode)) {
            exitMessage = messageSource.getMessage("BATCH_JOB_STATUS_STOPPED", new String[]{}, null);
        } else if ("ABANDONED".equals(exitCode)) {
            exitMessage = messageSource.getMessage("BATCH_JOB_STATUS_ABANDONED", new String[] {}, null);
        } else if ("UNKNOWN".equals(exitCode)) {
            exitMessage = messageSource.getMessage("BATCH_JOB_STATUS_UNKNOWN", new String[]{}, null);
        } else if ("FAILED".equals(exitCode)) {
            exitMessage = messageSource.getMessage("BATCH_JOB_STATUS_FAILED", new String[]{}, null);
        }
        jobExecution.setExitStatus(new ExitStatus(exitCode, exitMessage));
    }
}