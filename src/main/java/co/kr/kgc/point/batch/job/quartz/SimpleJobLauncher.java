package co.kr.kgc.point.batch.job.quartz;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class SimpleJobLauncher extends QuartzJobBean {
    private static final Logger logger = LogManager.getLogger(SimpleJobLauncher.class);

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("SimpleJob........................");
    }
}
