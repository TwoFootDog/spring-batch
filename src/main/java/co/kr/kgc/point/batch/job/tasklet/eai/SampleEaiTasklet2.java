package co.kr.kgc.point.batch.job.tasklet.eai;

import co.kr.kgc.point.batch.mapper.point.SamplePointMapper;
import co.kr.kgc.point.batch.mapper.pos.SamplePosMapper;
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
import java.util.List;
import java.util.Map;

public class SampleEaiTasklet2 implements Tasklet, StepExecutionListener {
    private static final Logger log = LogManager.getLogger(SampleEaiTasklet2.class);
    @Autowired
    private SamplePosMapper samplePosMapper;
    @Autowired
    private SamplePointMapper samplePointMapper;
    @Autowired
    private MessageSource messageSource;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        long jobExecutionId = stepExecution.getJobExecutionId();
        long stepExecutionId = stepExecution.getId();
        String jobName = stepExecution.getJobExecution().getJobInstance().getJobName();
        String stepName = stepExecution.getStepName();
        String startTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(stepExecution.getStartTime());

        log.info("[" + jobExecutionId + "|" + stepExecutionId + "] "
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
        String endTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(stepExecution.getEndTime());
        String exitCode = stepExecution.getExitStatus().getExitCode();
        String exitMessage = null;

        log.info("[" + jobExecutionId + "|" + stepExecutionId + "] "
                + "Batch step end. "
                + "jobName : [" + jobName + "]. "
                + "stepName : [" + stepName + "]. "
                + "startTime : [" + startTime + "]. "
                + "endTime : [" + endTime + "]");
        log.info("[" + jobExecutionId + "|" + stepExecutionId + "] "
                + "readCount : " + stepExecution.getReadCount()
                + "writeCount : " + stepExecution.getWriteCount()
                + "skipCount : " + stepExecution.getSkipCount()
                + "exitCode : [" + exitCode + "]");

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("readCount", stepExecution.getReadCount());
        resultMap.put("writeCount", stepExecution.getWriteCount());
        resultMap.put("skipCount", stepExecution.getSkipCount());
        resultMap.put("exitCode", stepExecution.getExitStatus().getExitCode());
        stepExecution.getJobExecution().setExecutionContext(new ExecutionContext(resultMap));

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

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        StepExecution stepExecution = chunkContext.getStepContext().getStepExecution();
        JobExecution jobExecution = stepExecution.getJobExecution();
        ExecutionContext jobExecutionContext = jobExecution.getExecutionContext();
        long jobExecutionId = jobExecution.getJobId();
        long stepExecutionId = stepExecution.getId();

        int readCount = Integer.parseInt(String.valueOf(jobExecutionContext.get("read_count"))); // Job 전체 처리 대상 건수
        int writeCount = Integer.parseInt(String.valueOf(jobExecutionContext.get("write_count"))); // Job 현재 처리 건수
        int skipCount = Integer.parseInt(String.valueOf(jobExecutionContext.get("skip_count"))); // Job 현재 에러 건수

        Map<String, Object> map = new HashMap<>();
        map.put("min_pos_seq", jobExecutionContext.get("min_pos_seq"));
        map.put("max_pos_seq", jobExecutionContext.get("max_pos_seq"));

        Map<String, Object> item = null;
        /* DB synchronization target table insert process */
        int result = 0;
        try {
            item = samplePosMapper.selectSamplePosData2(map);       // 처리 대상 조회(단건)
            if (!item.isEmpty()) {
                result = samplePointMapper.insertSampleData(item);  // 데이터 입력
                if (result == 0) {
                    // skip(에러) 건수 증가. 처리 계속
                    skipCount++;
                    stepExecution.setWriteSkipCount(skipCount);
                    log.info("[" + jobExecutionId + "|" + stepExecutionId + "] Batch insert fail. Batch name : [" + jobExecution.getJobInstance().getJobName() + "]");
                    stepContribution.setExitStatus(ExitStatus.EXECUTING);
                    return RepeatStatus.CONTINUABLE;
                }
            } else {
                // 배치 종료 처리
                log.info("[" + jobExecutionId + "|" + stepExecutionId + "] Insert target not found. Batch name : [" + jobExecution.getJobInstance().getJobName() + "]");
                stepContribution.setExitStatus(ExitStatus.COMPLETED);
                return RepeatStatus.FINISHED;
            }
        } catch(DuplicateKeyException e) {
            // skip(에러) 건수 증가. 처리 계속
            skipCount++;
            stepExecution.setWriteSkipCount(skipCount);
            log.info("[" + jobExecutionId + "|" + stepExecutionId + "] Batch insert duplicate Key error. Ignore. Batch Name : [" + jobExecution.getJobInstance().getJobName() + "]");
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
                    log.info("[" + jobExecutionId + "|" + stepExecutionId + "] Batch update fail. Batch Name : [" + jobExecution.getJobInstance().getJobName() + "]");
                    stepContribution.setExitStatus(ExitStatus.EXECUTING);
                    return RepeatStatus.CONTINUABLE;
                }
            } catch (Exception e) {
                e.printStackTrace();
                stepContribution.setExitStatus(ExitStatus.FAILED);
                return RepeatStatus.FINISHED;
            }
        }

        writeCount ++;   // 처리 건수 증가
        jobExecutionContext.remove("write_count");
        jobExecutionContext.put("write_count", writeCount);
        stepExecution.setWriteCount(writeCount);

        if (writeCount < readCount) {
            log.info("[" + jobExecutionId + "|" + stepExecutionId + "] "
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
}
