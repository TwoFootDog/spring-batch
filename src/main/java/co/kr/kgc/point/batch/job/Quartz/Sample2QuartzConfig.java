package co.kr.kgc.point.batch.job.Quartz;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

@PropertySource(value = "classpath:schedule.yml")
@RequiredArgsConstructor
@Configuration
@Data
public class Sample2QuartzConfig {
    private final static Logger logger = LogManager.getLogger(Sample2QuartzConfig.class);
    private final DataSource dataSource;
    private final PlatformTransactionManager transactionManager;
    private static final String TABLE_PREFIX = "BATCH_";
//    private final JobRepository jobRepository;

    private final JobLauncher jobLauncher;
    private final JobLocator jobLocator;
    private final Properties quartzProperties;

    @Value("${sample2.cron}")
    private String cronSchedule;

    /* Job 상세 정보를 설정하는 JobDetail */
    @Bean(name = "sample2JobDetail")
    public JobDetailFactoryBean jobDetailFactoryBean() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(Sample2QuartzJob.class);
        Map<String, Object> map = new HashMap<>();
        map.put("jobName", "sampleJob2");
        map.put("jobLauncher", jobLauncher);
        map.put("jobLocator", jobLocator);
        factoryBean.setJobDataAsMap(map);
        return factoryBean;
    }

    /* 배치 스케쥴링 정보를 설정하는 Trigger */
    @Bean(name = "sample2Trigger")
    public CronTriggerFactoryBean cronTriggerFactoryBean() {
        logger.info("cron 정보 : {}", cronSchedule);
        CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();
        factoryBean.setJobDetail(Objects.requireNonNull(jobDetailFactoryBean().getObject()));   // JobDetail 셋팅
        factoryBean.setCronExpression(cronSchedule);
        factoryBean.setName("sampleTrigger2");
        return factoryBean;
    }

    /* Trigger를 Scheduler에 등록 */
    @Bean(name = "sample2Scheduler")
    public SchedulerFactoryBean schedulerFactoryBean() {
        SchedulerFactoryBean factoryBean = new SchedulerFactoryBean();
        factoryBean.setQuartzProperties(quartzProperties);
        factoryBean.setTriggers(cronTriggerFactoryBean().getObject());  // Scheduler에 Trigger 셋팅
        logger.info("quartzProperty222 : {} ", quartzProperties);
        return factoryBean;
    }

}
