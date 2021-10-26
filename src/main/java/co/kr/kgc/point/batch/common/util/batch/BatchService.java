package co.kr.kgc.point.batch.common.util.batch;


import co.kr.kgc.point.batch.domain.BatchResponseDto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.*;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

/* BatchController에서 직접 호출해주는 Spring Batch 관련 서비스 */
@Service
public class BatchService {

    private static final Logger log = LogManager.getLogger(BatchService.class);

    private final JobLauncher jobLauncher;
    private final JobLocator jobLocator;
    private final JobOperator jobOperator;
    private final JobExplorer jobExplorer;
    private final MessageSource messageSource;

    public BatchService(JobLauncher jobLauncher,
                        JobLocator jobLocator,
                        JobOperator jobOperator,
                        JobExplorer jobExplorer,
                        MessageSource messageSource) {
        this.jobLauncher = jobLauncher;
        this.jobLocator = jobLocator;
        this.jobOperator = jobOperator;
        this.jobExplorer = jobExplorer;
        this.messageSource = messageSource;
    }

    /* Batch Job 즉시 실행 */
    public BatchResponseDto startJob(String jobName) {
        String requestDate = new SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis());
        JobParameters jobParameters = new JobParametersBuilder()
                                            .addString("--job.name", jobName)
                                            .addString("requestDate", requestDate)
                                            .toJobParameters();
        try {
            Job job = jobLocator.getJob(jobName);
            JobExecution jobExecution = jobLauncher.run(job, jobParameters);
            log.info("job start success. jobExecutionId : [" + jobExecution.getId() + "]");
            return new BatchResponseDto
                    .Builder()
                    .setjobName(jobName)
                    .setJobExecutionId(jobExecution.getId())
                    .setStartTime(new SimpleDateFormat("yyyyMMddHHmmss").format(jobExecution.getStartTime()))
                    .setResultCode(messageSource.getMessage("batch.response.success.code", new String[]{}, null))
                    .setResultMessage(messageSource.getMessage("batch.response.success.msg", new String[]{}, null))
                    .build();
        } catch (NoSuchJobException | JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
            log.error("Failed to start Job. jobName :  {}, message : {}", jobName, e.getMessage());
            return new BatchResponseDto
                    .Builder()
                    .setjobName(jobName)
                    .setResultCode(messageSource.getMessage("batch.response.fail.code", new String[]{}, null))
                    .setResultMessage(messageSource.getMessage("batch.response.fail.msg", new String[]{e.getMessage()}, null))
                    .build();
        }
    }

    /* Batch Job 즉시 중지. 트랜잭션이 다른 서비스는 트랜잭션 처리가 완료될 때까지 중지되지 않고(STOPPING)
    * 처리 완료 후 중지 처리된다(STOPPED) */
    public BatchResponseDto stopJob(long jobExecutionId) {
        JobExecution jobExecution = jobExplorer.getJobExecution(jobExecutionId);
        String jobName = jobExecution != null ? jobExecution.getJobInstance().getJobName() : "";
        Date startTime = jobExecution != null ? jobExecution.getStartTime() : null;

        try {
            boolean result = jobOperator.stop(jobExecutionId);
            if (result) {
                log.info("job stop success. jobExecutionId : [" + jobExecutionId + "]");
                return new BatchResponseDto
                        .Builder()
                        .setjobName(jobName)
                        .setJobExecutionId(jobExecutionId)
                        .setStartTime(new SimpleDateFormat("yyyyMMddHHmmss").format(startTime))
                        .setResultCode(messageSource.getMessage("batch.response.success.code", new String[]{}, null))
                        .setResultMessage(messageSource.getMessage("batch.response.success.msg", new String[]{}, null))
                        .build();
            } else {
                log.error("Failed to stop Job. jobName :  {}", jobName);
                return new BatchResponseDto
                        .Builder()
                        .setjobName(jobName)
                        .setResultCode(messageSource.getMessage("batch.response.fail.code", new String[]{}, null))
                        .setResultMessage(messageSource.getMessage("batch.response.fail.msg", new String[]{}, null))
                        .build();
            }
        } catch (NoSuchJobExecutionException | JobExecutionNotRunningException e) {
            log.error("Failed to stop Job. jobName :  {}, message : {}", jobName, e.getMessage());
            return new BatchResponseDto
                    .Builder()
                    .setjobName(jobName)
                    .setResultCode(messageSource.getMessage("batch.response.fail.code", new String[]{}, null))
                    .setResultMessage(messageSource.getMessage("batch.response.fail.msg", new String[]{e.getMessage()}, null))
                    .build();
        }
    }

    /* Batch Job 재실행 */
    public BatchResponseDto restartJob(long jobExecutionId) {
        JobExecution jobExecution = jobExplorer.getJobExecution(jobExecutionId);
        String jobName = jobExecution != null ? jobExecution.getJobInstance().getJobName() : "";
        Date startTime = jobExecution != null ? jobExecution.getStartTime() : null;

        try {
            long result = jobOperator.restart(jobExecutionId);
            if (result > 0) {
                log.info("job restart success. jobExecutionId : [" + result + "]");
                return new BatchResponseDto
                        .Builder()
                        .setjobName(jobName)
                        .setJobExecutionId(jobExecutionId)
                        .setStartTime(new SimpleDateFormat("yyyyMMddHHmmss").format(startTime))
                        .setResultCode(messageSource.getMessage("batch.response.success.code", new String[]{}, null))
                        .setResultMessage(messageSource.getMessage("batch.response.success.msg", new String[]{}, null))
                        .build();
            } else {
                log.error("Failed to restart Job. jobName :  {}", jobName);
                return new BatchResponseDto
                        .Builder()
                        .setjobName(jobName)
                        .setResultCode(messageSource.getMessage("batch.response.fail.code", new String[]{}, null))
                        .setResultMessage(messageSource.getMessage("batch.response.fail.msg", new String[]{}, null))
                        .build();
            }

        } catch (JobInstanceAlreadyCompleteException | NoSuchJobException |
                NoSuchJobExecutionException | JobParametersInvalidException |
                JobRestartException e) {
            e.printStackTrace();
            log.error("Failed to restart Job. jobName :  {}, message : {}", jobName, e.getMessage());
            return new BatchResponseDto
                    .Builder()
                    .setjobName(jobName)
                    .setResultCode(messageSource.getMessage("batch.response.fail.code", new String[]{}, null))
                    .setResultMessage(messageSource.getMessage("batch.response.fail.msg", new String[]{}, null))
                    .build();
        }
    }
}