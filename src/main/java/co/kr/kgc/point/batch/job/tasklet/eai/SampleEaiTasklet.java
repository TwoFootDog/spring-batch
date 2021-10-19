package co.kr.kgc.point.batch.job.tasklet.eai;

import co.kr.kgc.point.batch.mapper.point.SamplePointMapper;
import co.kr.kgc.point.batch.mapper.pos.SamplePosMapper;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobExecutionContext;
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
public class SampleEaiTasklet implements Tasklet, StepExecutionListener {

    private static final Logger log = LogManager.getLogger(SampleEaiTasklet.class);

    @Autowired
    private SamplePosMapper samplePosMapper;
    @Autowired
    private SamplePointMapper samplePointMapper;
    @Autowired
    private MessageSource messageSource;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        String jobName = stepExecution.getJobExecution().getJobInstance().getJobName();
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
        String jobName = stepExecution.getJobExecution().getJobInstance().getJobName();
        String exitCode = stepExecution.getExitStatus().getExitCode();
        String exitMessage = null;

        log.info(">>> batch step end. jobName : [" + jobName + "]. step name : [" + stepExecution.getReadCount() + "]");
        log.info(">>> Total Count: " + stepExecution.getReadCount());
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
        try {
            JobExecution jobExecution = chunkContext.getStepContext().getStepExecution().getJobExecution();
            ExecutionContext jobExecutionContext = jobExecution.getExecutionContext();
            StepExecution stepExecution = stepContribution.getStepExecution();

            Map<String, Object> item = samplePosMapper.selectSamplePosSeq();
            if (!item.isEmpty()) {
                jobExecutionContext.put("min_pos_seq", item.get("min_pos_seq"));
                jobExecutionContext.put("max_pos_seq", item.get("max_pos_seq"));
                jobExecutionContext.put("total_read_count", item.get("total_read_count"));
                jobExecutionContext.put("exec_count", 0);
                stepExecution.setReadCount(Integer.parseInt(String.valueOf(item.get("total_read_count"))));
            } else {
                log.info(">>> 처리 대상 미존재. batch Name : [" + jobExecution.getJobInstance().getJobName() + "]");
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
}
