package co.kr.kgc.point.batch.job.listener.eai;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class SampleStepListener implements StepExecutionListener {

    private static final Logger log = LogManager.getLogger(SampleStepListener.class);
    private final MessageSource messageSource;


    /* Step 시작 전 실행 */
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

    /* Step 완료 전 실행 */
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
            exitMessage = messageSource.getMessage("batch.job.completed.msg", new String[]{}, null);
        } else if ("FAILED".equals(exitCode)) {
            exitMessage = messageSource.getMessage("batch.job.failed.msg", new String[]{}, null);
        }
        return new ExitStatus(exitCode, exitMessage);
    }
}
