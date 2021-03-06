/*
 * @file : com.project.batch.domain.sample.tasklet.SampleDataSyncTasklet.java
 * @desc : 이기종 DB 간 데이터 동기화 진행을 위해 동기화 Source DB의 테이블(SYNC_SOURCE_TABLE) 전체 건수 및 SEQ 시작/종료값 조회하는 Tasklet
 * @auth :
 * @version : 1.0
 * @history
 * version (tag)     프로젝트명     일자      성명    변경내용
 * -------------    ----------   ------   ------  --------
 *
 * */

package com.project.batch.domain.sample.tasklet;

import com.project.batch.domain.common.util.CommonUtil;
import com.project.batch.domain.sample.mapper.firstDb.SampleFirstDbMapper;
import com.project.batch.domain.sample.mapper.secondDb.SampleSecondDbMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.*;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Map;

@Component
public class SampleDataSyncTasklet implements Tasklet, StepExecutionListener {
    private static final Logger log = LogManager.getLogger();
    private final SampleFirstDbMapper sampleFirstDbMapper;
    private final MessageSource messageSource;

    public SampleDataSyncTasklet(SampleFirstDbMapper sampleFirstDbMapper,
                                 MessageSource messageSource) {
        this.sampleFirstDbMapper = sampleFirstDbMapper;
        this.messageSource = messageSource;
    }
    /*
     * @method : execute
     * @desc : SampleDataSyncTasklet 메인 로직 수행(동기화 Source DB의 테이블(SYNC_SOURCE_TABLE) 전체 건수 및 SEQ 시작/종료값 조회)
     * @param :
     * @return :
     * */
    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        StepExecution stepExecution = null;
        JobExecution jobExecution = null;
        long jobExecutionId = 0;
        long stepExecutionId = 0;

        try {
            stepExecution = chunkContext.getStepContext().getStepExecution();
            jobExecution = stepExecution.getJobExecution();
            ExecutionContext jobExecutionContext = jobExecution.getExecutionContext();
            jobExecutionId = jobExecution.getId();
            stepExecutionId = stepExecution.getId();

            Map<String, Object> item = sampleFirstDbMapper.selectSyncSourceDataSeq();
            if (!CommonUtil.isEmpty(item)) {
                jobExecutionContext.put("minSeq", item.get("minSeq"));
                jobExecutionContext.put("maxSeq", item.get("maxSeq"));
                jobExecutionContext.put("totalReadCount", item.get("totalReadCount"));
                stepExecution.setReadCount(Integer.parseInt(String.valueOf(item.get("totalReadCount"))));
            } else {
                log.info("> [" + jobExecutionId + "|" + stepExecutionId + "] DB 동기화 대상 미존재. Batch name : [" + jobExecution.getJobInstance().getJobName() + "]");
                stepContribution.setExitStatus(ExitStatus.COMPLETED);
                return RepeatStatus.FINISHED;
            }
        } catch (Exception e) {
            log.info("> [" + jobExecutionId + "|" + stepExecutionId + "] DB 동기화 SEQ 및 건수 조회 Step Exception 에러. Batch name : [" +
                    jobExecution.getJobInstance().getJobName() + "]. message : [" +
                    e.getMessage() + "]");
            stepContribution.setExitStatus(ExitStatus.FAILED);
            return RepeatStatus.FINISHED;
        }
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


