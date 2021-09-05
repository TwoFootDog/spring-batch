package co.kr.kgc.point.batch.job.Quartz;

import lombok.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.text.SimpleDateFormat;

//@EqualsAndHashCode(callSuper = true)
//@Data
@Getter
@Setter
public class Sample2QuartzJob extends QuartzJobBean {

    private final static Logger logger = LogManager.getLogger(Sample2QuartzJob.class);
    private JobLauncher jobLauncher;
    private JobLocator jobLocator;
    private String jobName;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        JobParameters jobParameters = new JobParametersBuilder()
//                                            .addLong("requestDate", System.currentTimeMillis())
                                            .addString("requestDate", new SimpleDateFormat("yyyyMMddhhmmss").format(System.currentTimeMillis()))
                                            .toJobParameters();
//            JobExecution jobExecution = jobLauncher.run(job, jobParameters);
//            logger.info("Job was completed successfully!!", job.getName(), jobExecution.getId());
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        JobKey jobKey = jobExecutionContext.getJobDetail().getKey();
        logger.info("jobName : {}", jobDataMap.getString("jobName"));
        logger.info("jobKey : {}", jobKey);

        try {
            Job job = jobLocator.getJob(jobName);
//            jobLauncher.run(jobLocator.getJob(jobDataMap.getString("jobName")), jobParameters);
            JobExecution jobExecution = jobLauncher.run(job, jobParameters);
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException | JobParametersInvalidException | NoSuchJobException e) {
            e.printStackTrace();
        }

    }
}
