/*
 * @file : kr.co.kgc.point.batch.common.config.QuartzConfig.java
 * @desc : Quartz Scheduler를 위한 설정파일
 * @auth :
 * @version : 1.0
 * @history
 * version (tag)     프로젝트명     일자      성명    변경내용
 * -------------    ----------   ------   ------  --------
 *
 * */

package kr.co.kgc.point.batch.common.config;

import kr.co.kgc.point.batch.domain.common.quartz.SchedulerFactory;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.Properties;

@Configuration
public class QuartzConfig {

    private final ApplicationContext applicationContext;
    private final Environment environment;


    public QuartzConfig(ApplicationContext applicationContext,
                        Environment environment) {
        this.applicationContext = applicationContext;
        this.environment = environment;
    }

    /*
     * @method : quartzProperties
     * @desc : Quartz Scheduler 설정파일 불러오기(resources/quartz_dev.yml or resources/quartz_prod.yml)
     * @param :
     * @return :
     * */
    @Bean
    public Properties quartzProperties() throws Exception {
        YamlPropertiesFactoryBean factoryBean = new YamlPropertiesFactoryBean();
        if ("prod".equals(environment.getActiveProfiles())) {
            factoryBean.setResources(new ClassPathResource("/mybatis/mapper/quartz/quartz_prod.yml"));
        } else {
            factoryBean.setResources(new ClassPathResource("/mybatis/mapper/quartz/quartz_dev.yml"));
        }
        factoryBean.afterPropertiesSet();
        return factoryBean.getObject();
    }

    /*
     * @method : schedulerFactoryBean
     * @desc : Point 시스템의 Quartz Scheduler 생성을 위한 빈
     * @param :
     * @return :
     * */
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
