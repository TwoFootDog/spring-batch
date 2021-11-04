/*
 * @file : kr.co.kgc.point.batch.domain.sample.tasklet.SampleDataSyncTasklet.java
 * @desc : 이기종 DB 간 데이터 동기화 진행을 위해 동기화 Source DB의 테이블(POS_IF_TABLE1) 전체 건수 및 SEQ 시작/종료값 조회하는 Tasklet
 * @auth :
 * @version : 1.0
 * @history
 * version (tag)     프로젝트명     일자      성명    변경내용
 * -------------    ----------   ------   ------  --------
 *
 * */

package kr.co.kgc.point.batch.domain.sample.tasklet;

import kr.co.kgc.point.batch.domain.common.util.CommonUtil;
import kr.co.kgc.point.batch.domain.pos.mapper.SamplePosMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.*;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import java.text.SimpleDateFormat;
import java.util.Map;

public class SampleDataSyncTasklet implements Tasklet, StepExecutionListener {
    private static final Logger log = LogManager.getLogger();
    @Autowired
    private SamplePosMapper samplePosMapper;
    @Autowired
    private MessageSource messageSource;

    /*
     * @method : execute
     * @desc : SampleDataSyncTasklet 메인 로직 수행(동기화 Source DB의 테이블(POS_IF_TABLE1) 전체 건수 및 SEQ 시작/종료값 조회)
     * @param :
     * @return :
     * */
    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        try {
            StepExecution stepExecution = chunkContext.getStepContext().getStepExecution();
            JobExecution jobExecution = stepExecution.getJobExecution();
            ExecutionContext jobExecutionContext = jobExecution.getExecutionContext();
            long jobExecutionId = jobExecution.getId();
            long stepExecutionId = stepExecution.getId();

            
            Map<String, Object> item = samplePosMapper.selectSamplePosSeq();
            if (!CommonUtil.isEmpty(item)) {
                jobExecutionContext.put("minPosSeq", item.get("minPosSeq"));
                jobExecutionContext.put("maxPosSeq", item.get("maxPosSeq"));
                jobExecutionContext.put("totalReadCount", item.get("totalReadCount"));
                stepExecution.setReadCount(Integer.parseInt(String.valueOf(item.get("totalReadCount"))));
            } else {
                log.info("> [" + jobExecutionId + "|" + stepExecutionId + "] DB synchronization target not found. Batch name : [" + jobExecution.getJobInstance().getJobName() + "]");
                stepContribution.setExitStatus(ExitStatus.COMPLETED);
                return RepeatStatus.FINISHED;
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            exitMessage = messageSource.getMessage("batch.status.completed.msg", new String[]{}, null);
        } else if ("STOPPED".equals(exitCode)) {
            exitMessage = messageSource.getMessage("batch.status.stopped.msg", new String[] {}, null);
        } else if ("FAILED".equals(exitCode)) {
            exitMessage = messageSource.getMessage("batch.status.failed.msg", new String[]{}, null);
        }
        return new ExitStatus(exitCode, exitMessage);
    }
}


