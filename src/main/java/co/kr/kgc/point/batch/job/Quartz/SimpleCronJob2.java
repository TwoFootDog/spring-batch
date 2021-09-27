package co.kr.kgc.point.batch.job.Quartz;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class SimpleCronJob2 extends QuartzJobBean {
    private static final Logger logger = LogManager.getLogger(SimpleCronJob2.class);

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("SimpelCronJob2....");
    }
}
