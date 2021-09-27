package co.kr.kgc.point.batch.backup;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class SimpleQuartzJob extends QuartzJobBean {
    private static final Logger logger = LogManager.getLogger(SimpleQuartzJob.class);

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("SimpleQuartzJob.........Start......");
    }
}
