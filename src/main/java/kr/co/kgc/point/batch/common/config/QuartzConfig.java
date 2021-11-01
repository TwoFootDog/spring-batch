package kr.co.kgc.point.batch.common.config;


import kr.co.kgc.point.batch.domain.common.util.quartz.SchedulerFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.Properties;

/* Quartz Scheduler를 위한 설정파일 */
@Configuration
public class QuartzConfig {
    private static final Logger log = LogManager.getLogger(QuartzConfig.class);
    private final ApplicationContext applicationContext;

    public QuartzConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /* Quartz Scheduler 프로퍼티파일 불러오기(resources/quartz.yml) */
    @Bean
    public Properties quartzProperties() throws Exception{
        YamlPropertiesFactoryBean factoryBean = new YamlPropertiesFactoryBean();
        factoryBean.setResources(new ClassPathResource("/quartz.yml"));
        factoryBean.afterPropertiesSet();
        return factoryBean.getObject();
    }

    /* Point 시스템의 Quartz Scheduler 작업 생성을 위한 빈 */
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() throws Exception {
        SchedulerFactory jobFactory = new SchedulerFactory();
        jobFactory.setApplicationContext(applicationContext);

        SchedulerFactoryBean factoryBean = new SchedulerFactoryBean();
        factoryBean.setJobFactory(jobFactory);
        factoryBean.setQuartzProperties(quartzProperties());
        factoryBean.setOverwriteExistingJobs(true);
        return factoryBean;
    }
}
