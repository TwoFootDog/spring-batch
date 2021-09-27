//package co.kr.kgc.point.batch.job.Quartz;
//
//import lombok.Data;
//import lombok.RequiredArgsConstructor;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.springframework.batch.core.configuration.JobLocator;
//import org.springframework.batch.core.launch.JobLauncher;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.PropertySource;
//import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
//import org.springframework.scheduling.quartz.JobDetailFactoryBean;
//import org.springframework.scheduling.quartz.SchedulerFactoryBean;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Objects;
//import java.util.Properties;
//
//@PropertySource(value = "classpath:schedule.yml")
//@RequiredArgsConstructor
//@Configuration
//@Data
//public class SampleQuartzConfig {
//    private final static Logger logger = LogManager.getLogger(SampleQuartzConfig.class);
//    private final JobLauncher jobLauncher;
//    private final JobLocator jobLocator;
//    private final Properties quartzProperties;
//
//    @Value("${sample.cron}")
//    private String cronSchedule;
//
//    /* Job 상세 정보를 설정하는 JobDetail */
//    @Bean
//    public JobDetailFactoryBean jobDetailFactoryBean() {
//        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
//        factoryBean.setJobClass(SampleQuartzJob.class);
//        Map<String, Object> map = new HashMap<>();
//        map.put("jobName", "sampleJob");
//        map.put("jobLauncher", jobLauncher);
//        map.put("jobLocator", jobLocator);
//        factoryBean.setJobDataAsMap(map);
//        return factoryBean;
//    }
//
//    /* 배치 스케쥴링 정보를 설정하는 Trigger */
//    @Bean
//    public CronTriggerFactoryBean cronTriggerFactoryBean() {
//        logger.info("cron 정보 : {}", cronSchedule);
//        CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();
//        factoryBean.setJobDetail(Objects.requireNonNull(jobDetailFactoryBean().getObject()));   // JobDetail 셋팅
//        factoryBean.setCronExpression(cronSchedule);
//        factoryBean.setName("sampleTrigger");
//        return factoryBean;
//    }
//
//    /* Trigger를 Scheduler에 등록 */
//    @Bean
//    public SchedulerFactoryBean schedulerFactoryBean() {
//        SchedulerFactoryBean factoryBean = new SchedulerFactoryBean();
//        factoryBean.setQuartzProperties(quartzProperties);
//        factoryBean.setTriggers(cronTriggerFactoryBean().getObject());  // Scheduler에 Trigger 셋팅
////        logger.info("quartzProperty : {} ", quartzProperties);
//        return factoryBean;
//    }
//}
