package co.kr.kgc.point.batch.job.tasklet.point;

import co.kr.kgc.point.batch.common.util.CommonUtil;
import co.kr.kgc.point.batch.domain.SampleResponseDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class SampleInterfaceTasklet implements Tasklet, StepExecutionListener {
    private static final Logger log = LogManager.getLogger();

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private RestTemplate restTemplate;

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

        /* single data call */
        Map<String, Object> responseMap =
                restTemplate.exchange("https://reqres.in/api/users/2",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<Map<String, Object>>() {
                        }).getBody();

        if (!CommonUtil.isEmpty(responseMap)) {
            log.info("> Single Data Call success. response data : {}", responseMap.get("data"));
        }

        /* list data call */
        SampleResponseDto responseList =
                restTemplate.exchange("https://reqres.in/api/users?page=2",
                        HttpMethod.GET,
                        null,
                        SampleResponseDto.class).getBody();

        if (!CommonUtil.isEmpty(responseList)) {
            log.info("> List Data Call success. response data : {}", responseList);
        }

//        restTemplate.exchange("")


        stepContribution.setExitStatus(ExitStatus.COMPLETED);
        return RepeatStatus.FINISHED;
    }
}
