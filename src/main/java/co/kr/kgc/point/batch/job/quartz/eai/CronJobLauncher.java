package co.kr.kgc.point.batch.job.quartz.eai;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.text.SimpleDateFormat;

//@DisallowConcurrentExecution    // 동시수행 방지(클러스터 환경에서는 작동하지 않음. 테스트 필요)
public class CronJobLauncher extends QuartzJobBean {
    private static final Logger log = LogManager.getLogger(CronJobLauncher.class);
    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private JobLocator jobLocator;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("requestDate", new SimpleDateFormat("yyyyMMddhhmmssSSS").format(System.currentTimeMillis()))
                .toJobParameters();

        String jobName = jobExecutionContext.getJobDetail().getKey().getName();
        log.info(">> Cron job start. jobName : {}", jobName);

        try {
            Job job = jobLocator.getJob(jobName);
            JobExecution jobExecution = jobLauncher.run(job, jobParameters);
        } catch (JobExecutionAlreadyRunningException
                | JobRestartException
                | JobInstanceAlreadyCompleteException
                | JobParametersInvalidException
                | NoSuchJobException e) {
            e.printStackTrace();
        }
    }
}
