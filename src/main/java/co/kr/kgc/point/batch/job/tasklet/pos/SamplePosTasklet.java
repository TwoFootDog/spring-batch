package co.kr.kgc.point.batch.job.tasklet.pos;

import co.kr.kgc.point.batch.mapper.pos.SamplePosMapper;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

//@RequiredArgsConstructor
public class  SamplePosTasklet implements Tasklet, StepExecutionListener {
    private static final Logger log = LogManager.getLogger(SamplePosTasklet.class);
    @Autowired
    private SamplePosMapper samplePosMapper;
    @Autowired
    private MessageSource messageSource;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        String stepName = stepExecution.getStepName();
        long stepId = stepExecution.getId();
        long jobExecutionId = stepExecution.getJobExecutionId();
        String startTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(stepExecution.getStartTime());

        log.info(">> batch Step Start. " +
                "stepName : [" + stepName +
                "]. stepId : ["  + stepId +
                "]. startTime : [" + startTime + "]" );
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        String jobName = stepExecution.getJobExecution().getJobInstance().getJobName();
        String exitCode = stepExecution.getExitStatus().getExitCode();
        String exitMessage = null;

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("readCount", stepExecution.getReadCount());
        resultMap.put("writeCount", stepExecution.getWriteCount());
        resultMap.put("skipCount", stepExecution.getSkipCount());
        resultMap.put("exitCode", stepExecution.getExitStatus().getExitCode());
        stepExecution.getJobExecution().setExecutionContext(new ExecutionContext(resultMap));

        log.info(">>> Total Count: " + stepExecution.getReadCount());
        log.info(">>> Success Count: " + stepExecution.getWriteCount());
        log.info(">>> Faild Count: " + stepExecution.getWriteSkipCount());
        log.info(">>> exitCode : " + exitCode);

        /* exit message setting */
        if ("COMPLETED".equals(exitCode)) {
            exitMessage = messageSource.getMessage("batch.status.completed.msg", new String[]{}, null);
        } else if ("FAILED".equals(exitCode)) {
            exitMessage = messageSource.getMessage("batch.status.failed.msg", new String[]{}, null);
        }
        return new ExitStatus(exitCode, exitMessage);
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        for (int i = 1; i<=20000; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("column1", i);
            map.put("column2", i);
            int result = samplePosMapper.insertSamplePosData(map);
            log.info("Sample Pos Data : " + i + "번째 데이터 결과 : " + result);

        }
        stepContribution.setExitStatus(ExitStatus.COMPLETED);
        return RepeatStatus.FINISHED;
    }
}
