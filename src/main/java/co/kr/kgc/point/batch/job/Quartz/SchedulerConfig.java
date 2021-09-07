package co.kr.kgc.point.batch.job.Quartz;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.SchedulerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class SchedulerConfig {
    private final DataSource dataSource;
    private final ApplicationContext applicationContext;
    private final Properties quartzProperties;

    private static final Logger logger = LogManager.getLogger(SchedulerConfig.class);

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        logger.info(".........SchedulerConfig.......");
        SchedulerFactoryBean  factoryBean = new SchedulerFactoryBean();
        factoryBean.setApplicationContext(applicationContext);
        factoryBean.setQuartzProperties(quartzProperties);
        factoryBean.setOverwriteExistingJobs(true);
        factoryBean.setDataSource(dataSource);
        return factoryBean;
    }
}
