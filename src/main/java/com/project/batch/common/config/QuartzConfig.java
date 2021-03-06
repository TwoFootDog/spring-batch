/*
 * @file : com.project.batch.common.config.QuartzConfig.java
 * @desc : Quartz Scheduler를 위한 설정파일
 * @auth :
 * @version : 1.0
 * @history
 * version (tag)     프로젝트명     일자      성명    변경내용
 * -------------    ----------   ------   ------  --------
 *
 * */

package com.project.batch.common.config;

import com.project.batch.domain.common.quartz.SchedulerFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.Arrays;
import java.util.Properties;

@Configuration
public class QuartzConfig {

    private final ApplicationContext applicationContext;
    private final Environment environment;
    private static final Logger log = LogManager.getLogger();


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
        if (environment.acceptsProfiles(Profiles.of("prod"))) {
            factoryBean.setResources(new ClassPathResource("/quartz/quartz_prod.yml"));
        } else {
            factoryBean.setResources(new ClassPathResource("/quartz/quartz_dev.yml"));
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
