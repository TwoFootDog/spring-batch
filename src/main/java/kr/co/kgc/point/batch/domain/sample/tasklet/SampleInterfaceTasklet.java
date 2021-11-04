/*
 * @file : kr.co.kgc.point.batch.domain.sample.tasklet.SampleInterfaceTasklet.java
 * @desc : RestTemplate을 사용해서 외부 API 호출하는 Tasklet
 * @auth :
 * @version : 1.0
 * @history
 * version (tag)     프로젝트명     일자      성명    변경내용
 * -------------    ----------   ------   ------  --------
 *
 * */

package kr.co.kgc.point.batch.domain.sample.tasklet;

import kr.co.kgc.point.batch.domain.common.util.CommonUtil;
import kr.co.kgc.point.batch.domain.sample.dto.SampleListGetResponseDto;
import kr.co.kgc.point.batch.domain.sample.dto.SamplePostRequestDto;
import kr.co.kgc.point.batch.domain.sample.dto.SampleGetResponseDto;
import kr.co.kgc.point.batch.domain.sample.dto.SamplePostResponseDto;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;

public class SampleInterfaceTasklet implements Tasklet, StepExecutionListener {
    private static final Logger log = LogManager.getLogger();
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private RestTemplate restTemplate;
    private static final String SINGLE_DATA_GET_URL = "https://reqres.in/api/users/2";
    private static final String LIST_DATA_GET_URL = "https://reqres.in/api/users?page=2";
    private static final String SINGLE_DATA_POST_URL = "https://reqres.in/api/users";

    /*
     * @method : execute
     * @desc : SampleInterfaceTasklet 메인 로직 수행(RestTemplate을 사용해서 외부 API)
     * @param :
     * @return :
     * */
    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        /* get api call(single data get) */
        SampleGetResponseDto response =
                restTemplate.exchange(SINGLE_DATA_GET_URL,
                        HttpMethod.GET,
                        null,
                        SampleGetResponseDto.class).getBody();

        if (!CommonUtil.isEmpty(response)) {
            log.info("> Single Data Get success. response data : {}", response.getData());
        }

        /* get api call(list data get) */
        SampleListGetResponseDto listResponse =
                restTemplate.exchange(LIST_DATA_GET_URL,
                        HttpMethod.GET,
                        null,
                        SampleListGetResponseDto.class).getBody();

        if (!CommonUtil.isEmpty(listResponse)) {
            log.info("> List Data Get success. response data : {}", listResponse);
        }

        Thread.sleep(10000);

        /* post api call*/
        HttpEntity<SamplePostRequestDto> requestDto =
                new HttpEntity<>(new SamplePostRequestDto
                        .Builder()
                        .name("morpheus")
                        .job("leader")
                        .build());
        SamplePostResponseDto response2 =
                restTemplate.exchange(SINGLE_DATA_POST_URL,
                        HttpMethod.POST,
                        requestDto,
                        SamplePostResponseDto.class).getBody();
        if (!CommonUtil.isEmpty(response2)) {
            log.info("> Single Data Post success. response data : {}", listResponse);
        }

        stepContribution.setExitStatus(ExitStatus.COMPLETED);
        return RepeatStatus.FINISHED;
    }

    /*
     * @method : beforeStep
     * @desc : SampleInterfaceTasklet 메인 로직 시작 전 실행
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
     * @desc : SampleInterfaceTasklet 메인 로직 후 실행
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
