package kr.co.kgc.point.batch.domain.sample.tasklet;

import kr.co.kgc.point.batch.domain.common.util.CommonUtil;
import kr.co.kgc.point.batch.domain.point.mapper.SamplePointMapper;
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
import org.springframework.dao.DuplicateKeyException;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class SampleEaiTasklet2 implements Tasklet, StepExecutionListener {
    private static final Logger log = LogManager.getLogger();
    @Autowired
    private SamplePosMapper samplePosMapper;
    @Autowired
    private SamplePointMapper samplePointMapper;
    @Autowired
    private MessageSource messageSource;

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
            item = samplePosMapper.selectSamplePosData2(map);       // 처리 대상 조회(단건)
            if (!CommonUtil.isEmpty(item)) {
                readCount++;
                result = samplePointMapper.insertSampleData(item);  // 데이터 입력
                if (result == 0) {
                    log.info("> [" + jobExecutionId + "|" + stepExecutionId + "] Fail to insert data. Batch name : [" + jobExecution.getJobInstance().getJobName() + "]");
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
            log.info("> [" + jobExecutionId + "|" + stepExecutionId + "] Fail to insert data(Dup key error). Ignore. Batch Name : [" + jobExecution.getJobInstance().getJobName() + "]");
        } catch (Exception e) {
            // 배치 종료 처리
            e.printStackTrace();
            stepContribution.setExitStatus(ExitStatus.FAILED);
            return RepeatStatus.FINISHED;
        }

        if (result > 0) {
            try {
                int result2 = samplePosMapper.updateSamplePosData(item);
                if (result2 == 0) {
                    log.info("> [" + jobExecutionId + "|" + stepExecutionId + "] Fail to update data. Batch Name : [" + jobExecution.getJobInstance().getJobName() + "]");
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
            log.info("> [" + jobExecutionId + "|" + stepExecutionId + "] "
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

        /* exit message setting */
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
