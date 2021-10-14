package co.kr.kgc.point.batch.job.listener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

import java.text.SimpleDateFormat;

public class SampleStepListener implements StepExecutionListener {

    private static final Logger log = LogManager.getLogger(SampleStepListener.class);

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
        log.info(">>>> batch Step End. StepExecution : [" + stepExecution + "]" );

        return new ExitStatus(stepExecution.getExitStatus().getExitCode(), "hahaha");
    }
}
