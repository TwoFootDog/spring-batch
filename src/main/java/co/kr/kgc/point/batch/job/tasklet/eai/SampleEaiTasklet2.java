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

//@RequiredArgsConstructor
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
        String stepName = stepExecution.getStepName();
        long stepId = stepExecution.getId();
        String startTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(stepExecution.getStartTime());

        log.info(">> batch Step Start. " +
                "stepName : [" + stepName +
                "]. stepId : ["  + stepId +
                "]. startTime : [" + startTime + "]" );
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        String stepName = stepExecution.getStepName();
        String exitCode = stepExecution.getExitStatus().getExitCode();
        String exitMessage = null;

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("readCount", stepExecution.getReadCount());
        resultMap.put("writeCount", stepExecution.getWriteCount());
        resultMap.put("skipCount", stepExecution.getSkipCount());
        resultMap.put("exitCode", stepExecution.getExitStatus().getExitCode());
        stepExecution.getJobExecution().setExecutionContext(new ExecutionContext(resultMap));

        log.info(">>> batch step end. step name : [" + stepExecution.getReadCount() + "]");
        log.info(">>> Total Count: " + stepExecution.getReadCount());
        log.info(">>> Success Count: " + stepExecution.getWriteCount());
        log.info(">>> Faild Count: " + stepExecution.getWriteSkipCount());
        log.info(">>> exitCode : " + exitCode);

        /* exit message setting */
        if ("COMPLETED".equals(exitCode)) {
            exitMessage = messageSource.getMessage("batch.job.completed.msg", new String[]{}, null);
        } else if ("FAILED".equals(exitCode)) {
            exitMessage = messageSource.getMessage("batch.job.failed.msg", new String[]{}, null);
        }
        return new ExitStatus(exitCode, exitMessage);
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        JobExecution jobExecution = chunkContext.getStepContext().getStepExecution().getJobExecution();
        ExecutionContext jobExecutionContext = jobExecution.getExecutionContext();

        int totalReadCount = Integer.parseInt(String.valueOf(jobExecutionContext.get("total_read_count"))); // 전체 조회 건수
        int execCount = Integer.parseInt(String.valueOf(jobExecutionContext.get("exec_count"))); // 현재 처리 건수

        Map<String, Object> map = new HashMap<>();
        map.put("min_pos_seq", jobExecutionContext.get("min_pos_seq"));
        map.put("max_pos_seq", jobExecutionContext.get("max_pos_seq"));

        Map<String, Object> item = null;
        int result = 0;
        try {
            item = samplePosMapper.selectSamplePosData2(map);       // 처리 대상 조회(단건)
            if (!item.isEmpty()) {
                result = samplePointMapper.insertSampleData(item);  // 데이터 입력
                if (result == 0) {
                    log.info(">>> INSERT FAIL. batch Name : [" + jobExecution.getJobInstance().getJobName() + "]");
                    stepContribution.setExitStatus(ExitStatus.EXECUTING);
                    return RepeatStatus.CONTINUABLE;
                }
            } else {
                log.info(">>> 처리 대상 미존재. batch Name : [" + jobExecution.getJobInstance().getJobName() + "]");
                stepContribution.setExitStatus(ExitStatus.COMPLETED);
                return RepeatStatus.FINISHED;
            }
        } catch(DuplicateKeyException e) {
            log.info(">>> INSERT DUP KEY ERROR. 처리 계속. batch Name : [" + jobExecution.getJobInstance().getJobName() + "]");
        } catch (Exception e) {
            e.printStackTrace();
            stepContribution.setExitStatus(ExitStatus.FAILED);
            return RepeatStatus.FINISHED;
        }

        if (result > 0) {
            try {
                int result2 = samplePosMapper.updateSamplePosData(item);
                if (result2 == 0) {
                    log.info(">>> UPDATE FAIL. batch Name : [" + jobExecution.getJobInstance().getJobName() + "]");
                    stepContribution.setExitStatus(ExitStatus.EXECUTING);
                    return RepeatStatus.CONTINUABLE;
                }
            } catch (Exception e) {
                e.printStackTrace();
                stepContribution.setExitStatus(ExitStatus.FAILED);
                return RepeatStatus.FINISHED;
            }
        }

        execCount ++;   // 처리 건수 증가
        jobExecutionContext.remove("exec_count");
        jobExecutionContext.put("exec_count", execCount);
        if (execCount < totalReadCount) {
            log.info(">> batch executing... execCount : [" + execCount + "]. totalReadCount : [" + totalReadCount + "]");
            stepContribution.setExitStatus(ExitStatus.EXECUTING);
            return RepeatStatus.CONTINUABLE;
        }

        log.info("batch end.. execCount : [" + execCount + "]. totalReadCount : [" + totalReadCount + "]");
        stepContribution.setExitStatus(ExitStatus.COMPLETED);
        return RepeatStatus.FINISHED;
    }
}
