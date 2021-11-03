package kr.co.kgc.point.batch.domain.common.util.quartz;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

//@DisallowConcurrentExecution    // 동시수행 방지(클러스터 환경에서는 작동하지 않음. 테스트 필요)
public class CronJobLauncher extends QuartzJobBean {
    private static final Logger log = LogManager.getLogger();
    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private JobLocator jobLocator;
    @Autowired
    private JobExplorer jobExplorer;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("requestDate",
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")))
                .toJobParameters();

        String jobName = jobExecutionContext.getJobDetail().getKey().getName();
        try {
            Job job = jobLocator.getJob(jobName);

            /* 동일한 Job 명을 가진 배치가 실행 중인 경우 미실행 */
            if (jobExplorer.findRunningJobExecutions(jobName).size() < 1) {
                JobExecution jobExecution = jobLauncher.run(job, jobParameters);
                log.info(">> [" + jobExecution.getId() + "] Job Schedule start. " +
                         "jobName : [" + jobName + "]. " +
                        "jobExecutionId : [" + jobExecution.getId() + "]. ");
            }
        } catch (JobExecutionAlreadyRunningException
                | JobRestartException
                | JobInstanceAlreadyCompleteException
                | JobParametersInvalidException
                | NoSuchJobException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
