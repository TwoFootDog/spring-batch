/*
 * @file : com.project.batch.domain.sample.tasklet.SampleDataSync2Tasklet.java
 * @desc : 동기화 Target DB의 테이블(POINT_TABLE1)에 데이터 INSERT 후,
 *         동기화 Source DB의 테이블(POS_IF_TABLE1)에 동기화 처리 결과 UPDATE하는 Tasklet
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
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

@Component
public class SampleDataSync2Tasklet implements Tasklet, StepExecutionListener {
    private static final Logger log = LogManager.getLogger();
    private final SampleFirstDbMapper sampleFirstDbMapper;
    private final SampleSecondDbMapper sampleSecondDbMapper;
    private final MessageSource messageSource;

    public SampleDataSync2Tasklet(SampleFirstDbMapper sampleFirstDbMapper,
                                  SampleSecondDbMapper sampleSecondDbMapper,
                                  MessageSource messageSource) {
        this.sampleFirstDbMapper = sampleFirstDbMapper;
        this.sampleSecondDbMapper = sampleSecondDbMapper;
        this.messageSource = messageSource;
    }


//    @Autowired
//    private SampleSecondDbMapper sampleSecondDbMapper;
//    @Autowired
//    private SampleFirstDbMapper sampleFirstDbMapper;
//    @Autowired
//    private MessageSource messageSource;

    /*
     * @method : execute
     * @desc : SampleDataSync2Tasklet 메인 로직 수행(동기화 Target DB의 테이블(POINT_TABLE1)에 데이터 INSERT 후,
     *         동기화 Source DB의 테이블(POS_IF_TABLE1)에 동기화 처리 결과 UPDATE하는 Tasklet)
     * @param :
     * @return :
     * */
    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        StepExecution stepExecution = chunkContext.getStepContext().getStepExecution();
        JobExecution jobExecution = stepExecution.getJobExecution();
        ExecutionContext jobExecutionContext = jobExecution.getExecutionContext();
        long jobExecutionId = jobExecution.getId();
        long stepExecutionId = stepExecution.getId();

        int totalReadCount = Integer.parseInt(String.valueOf(jobExecutionContext.get("totalReadCount"))); // Job 처리 대상 건수
        int readCount = stepExecution.getWriteCount(); // step의 현재 read 건수
        int writeCount = stepExecution.getWriteCount(); // step의 현재 write 건수
        int skipCount = stepExecution.getSkipCount();   // step의 현재 skip(error) 건수

        Map<String, Object> map = new HashMap<>();
        map.put("minPosSeq", jobExecutionContext.get("minPosSeq"));
        map.put("maxPosSeq", jobExecutionContext.get("maxPosSeq"));

        Map<String, Object> item = null;
        /* DB synchronization target table insert process */
        int result = 0;
        try {
            item = sampleSecondDbMapper.selectSamplePosData2(map);       // 처리 대상 조회(단건)
            if (!CommonUtil.isEmpty(item)) {
                readCount++;
                result = sampleFirstDbMapper.insertSampleData(item);  // 데이터 입력
                if (result == 0) {
                    log.error("> [" + jobExecutionId + "|" + stepExecutionId + "] Fail to insert data. Batch name : [" + jobExecution.getJobInstance().getJobName() + "]");
                    stepContribution.setExitStatus(ExitStatus.FAILED);
                    return RepeatStatus.FINISHED;
                }
            } else {
                // 배치 종료 처리
                log.info("> [" + jobExecutionId + "|" + stepExecutionId + "] Insert target not found. Batch name : [" + jobExecution.getJobInstance().getJobName() + "]");
                stepContribution.setExitStatus(ExitStatus.COMPLETED);
                return RepeatStatus.FINISHED;
            }
        } catch(DuplicateKeyException e) {
            // skip(에러) 건수 증가. 처리 계속
            skipCount++;
            result++;
            log.error("> [" + jobExecutionId + "|" + stepExecutionId + "] Fail to insert data(Dup key error). Ignore. Batch Name : [" + jobExecution.getJobInstance().getJobName() + "]");
        } catch (Exception e) {
            // 배치 종료 처리
            e.printStackTrace();
            stepContribution.setExitStatus(ExitStatus.FAILED);
            return RepeatStatus.FINISHED;
        }

        if (result > 0) {
            try {
                int result2 = sampleSecondDbMapper.updateSamplePosData(item);
                if (result2 == 0) {
                    log.error("> [" + jobExecutionId + "|" + stepExecutionId + "] Fail to update data. Batch Name : [" + jobExecution.getJobInstance().getJobName() + "]");
                    stepContribution.setExitStatus(ExitStatus.FAILED);
                    return RepeatStatus.FINISHED;
                }
            } catch (Exception e) {
                e.printStackTrace();
                stepContribution.setExitStatus(ExitStatus.FAILED);
                return RepeatStatus.FINISHED;
            }
        }

        // 처리 건수 증가 및 step의 read/write/skip count 셋팅
        writeCount++;
        stepExecution.setReadCount(readCount);
        stepExecution.setWriteCount(writeCount);
        stepExecution.setWriteSkipCount(skipCount);

        // step에서 조회 건수
        if (readCount < totalReadCount) {
            log.debug("> [" + jobExecutionId + "|" + stepExecutionId + "] "
                    + "Batch Step Executing. "
                    + "readCount : [" + readCount + "]. "
                    + "writeCount : [" + writeCount + "]. "
                    + "skipCount : [" + skipCount + "]");
            stepContribution.setExitStatus(ExitStatus.EXECUTING);
            return RepeatStatus.CONTINUABLE;
        }

        stepContribution.setExitStatus(ExitStatus.COMPLETED);
        return RepeatStatus.FINISHED;
    }


    /*
     * @method : beforeStep
     * @desc : SampleDataSync2Tasklet 메인 로직 시작 전 실행
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
                + "Batch step start. "
                + "jobName : [" + jobName + "]."
                + "stepName : [" + stepName + "]. "
                + "startTime : [" + startTime + "]" );
    }

    /*
     * @method : afterStep
     * @desc : SampleDataSync2Tasklet 메인 로직 후 실행
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
                + "writeCount : [" + stepExecution.getWriteCount() + "]. "
                + "skipCount : [" + stepExecution.getSkipCount() + "]. "
                + "exitCode : [" + exitCode + "]");

        stepExecution.getJobExecution().getExecutionContext().put("readCount", stepExecution.getReadCount());
        stepExecution.getJobExecution().getExecutionContext().put("writeCount", stepExecution.getWriteCount());
        stepExecution.getJobExecution().getExecutionContext().put("skipCount", stepExecution.getSkipCount());
        stepExecution.getJobExecution().getExecutionContext().put("exitCode", stepExecution.getExitStatus().getExitCode());

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
