package co.kr.kgc.point.batch.config;


import co.kr.kgc.point.batch.job.quartz.util.SchedulerJobFactory;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

/* Quartz Scheduler를 위한 설정파일 */
@Configuration
@RequiredArgsConstructor
public class QuartzConfig {
    private final DataSource dataSource;
    private final ApplicationContext applicationContext;

    private static final Logger logger = LogManager.getLogger(QuartzConfig.class);

    /* Quartz Scheduler 프로퍼티파일 불러오기(resources/quartz.yml) */
    @Bean
    public Properties quartzProperties() {
        YamlPropertiesFactoryBean factoryBean = new YamlPropertiesFactoryBean();
        factoryBean.setResources(new ClassPathResource("quartz.yml"));
        factoryBean.afterPropertiesSet();
        return factoryBean.getObject();
    }

    /* Point 시스템의 Quartz Scheduler 작업 생성을 위한 빈 */
    @Bean
    public SchedulerFactoryBean pointSchedulerFactoryBean() {
        SchedulerJobFactory jobFactory = new SchedulerJobFactory();
        jobFactory.setApplicationContext(applicationContext);

        SchedulerFactoryBean factoryBean = new SchedulerFactoryBean();
        factoryBean.setJobFactory(jobFactory);
//        factoryBean.setApplicationContext(applicationContext);
        factoryBean.setQuartzProperties(quartzProperties());
        factoryBean.setOverwriteExistingJobs(true);
        factoryBean.setDataSource(dataSource);
        return factoryBean;
    }
}
