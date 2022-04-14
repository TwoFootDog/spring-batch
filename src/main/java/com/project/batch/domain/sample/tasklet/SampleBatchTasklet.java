/*
 * @file : com.project.batch.domain.sample.tasklet.SampleDataSyncTasklet.java
 * @desc : Sample Batch Tasklet
 * @auth :
 * @version : 1.0
 * @history
 * version (tag)     프로젝트명     일자      성명    변경내용
 * -------------    ----------   ------   ------  --------
 *
 * */

package com.project.batch.domain.sample.tasklet;

import com.project.batch.domain.sample.mapper.firstDb.SampleFirstDbMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

@Component
@StepScope
public class SampleBatchTasklet implements Tasklet, StepExecutionListener {
    private static final Logger log = LogManager.getLogger();
    private final SampleFirstDbMapper sampleFirstDbMapper;
    private final MessageSource messageSource;
    private final String jobName;
    private final String args1;
    private final String args2;

    public SampleBatchTasklet(SampleFirstDbMapper sampleFirstDbMapper,
                              MessageSource messageSource,
                              @Value("#{jobParameters['--job.name']}") String jobName,
                              @Value("#{jobParameters[args1]}") String args1,
                              @Value("#{jobParameters[args2]}") String args2) {
        this.sampleFirstDbMapper = sampleFirstDbMapper;
        this.messageSource = messageSource;
        this.jobName = jobName;
        this.args1 = args1;
        this.args2 = args2;
    }
    /*
     * @method : execute
     * @desc :
     * @param :
     * @return :
     * */
    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        StepExecution stepExecution = null;
        JobExecution jobExecution = null;
        long jobExecutionId = 0;
        long stepExecutionId = 0;

        log.info(">> SampleBatchTasklet Start>>>>> ");
        log.info(">> jobName : [" + jobName + "]. args1 : [" + args1 + "]. args2 : [" + args2 + "]");
        Thread.sleep(30000);
        log.info(">> SampleBatchTasklet End>>>>> ");

        stepContribution.setExitStatus(ExitStatus.COMPLETED);
        return RepeatStatus.FINISHED;
    }


    /*
     * @method : beforeStep
     * @desc : SampleDataSyncTasklet 메인 로직 시작 전 실행
     * @param :
     * @return :
     * */
    @Override
    public void beforeStep(StepExecution stepExecution) {
        long jobExecutionId = stepExecution.getJobExecutionId();
        long stepExecutionId = stepExecution.getId();
        String jobName = stepExecution.getJobExecution().getJobInstance().getJobName();
        String stepName = stepExecution.getStepName();
        String startTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(stepExecution.getStartTime());

        log.info("> [" + jobExecutionId + "|" + stepExecutionId + "] "
                + "Batch Step Start. "
                + "jobName : [" + jobName + "]."
                + "stepName : [" + stepName + "]. "
                + "startTime : [" + startTime + "]" );
    }

    /*
     * @method : afterStep
     * @desc : SampleDataSyncTasklet 메인 로직 후 실행
     * @param :
     * @return :
     * */
    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        long jobExecutionId = stepExecution.getJobExecutionId();
        long stepExecutionId = stepExecution.getId();
        String jobName = stepExecution.getJobExecution().getJobInstance().getJobName();
        String stepName = stepExecution.getStepName();
        String startTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(stepExecution.getStartTime());
        String endTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(System.currentTimeMillis());
        String exitCode = stepExecution.getExitStatus().getExitCode();
        String exitMessage = null;

        log.info("> [" + jobExecutionId + "|" + stepExecutionId + "] "
                + "Batch step end. "
                + "jobName : [" + jobName + "]. "
                + "stepName : [" + stepName + "]. "
                + "startTime : [" + startTime + "]. "
                + "endTime : [" + endTime + "]");
        log.info("> [" + jobExecutionId + "|" + stepExecutionId + "] "
                + "readCount : [" + stepExecution.getReadCount() + "]. "
                + "exitCode : [" + exitCode + "]");

        /* Batch Step 처리 결과 상태 및 메시지 리턴(BATCH_STEP_EXECUTION 테이블의 EXIT_CODE, EXIT_MESSAGE) */
        if ("COMPLETED".equals(exitCode)) {
            exitMessage = messageSource.getMessage("BATCH_STEP_STATUS_COMPLETED", new String[]{}, null);
        } else if ("STOPPED".equals(exitCode)) {
            exitMessage = messageSource.getMessage("BATCH_STEP_STATUS_STOPPED", new String[]{}, null);
        } else if ("ABANDONED".equals(exitCode)) {
            exitMessage = messageSource.getMessage("BATCH_STEP_STATUS_ABANDONED", new String[] {}, null);
        } else if ("UNKNOWN".equals(exitCode)) {
            exitMessage = messageSource.getMessage("BATCH_STEP_STATUS_UNKNOWN", new String[]{}, null);
        } else if ("FAILED".equals(exitCode)) {
            exitMessage = messageSource.getMessage("BATCH_STEP_STATUS_FAILED", new String[]{}, null);
        }
        return new ExitStatus(exitCode, exitMessage);
    }
}


