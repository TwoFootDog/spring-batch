/*
 * @file : com.project.batch.domain.sample.job.SampleBatchJobConfig.java
 * @desc : Sample Batch Job Config 클래스
 * @auth :
 * @version : 1.0
 * @history
 * version (tag)     프로젝트명     일자      성명    변경내용
 * -------------    ----------   ------   ------  --------
 *
 * */

package com.project.batch.domain.sample.job;

import com.project.batch.domain.common.listener.CommonJobListener;
import com.project.batch.domain.sample.tasklet.SampleBatchTasklet;
import com.project.batch.domain.sample.tasklet.SampleDataSync2Tasklet;
import com.project.batch.domain.sample.tasklet.SampleDataSyncTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

@Configuration
public class SampleBatchJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSourceTransactionManager firstDbTransactionManager;

    public SampleBatchJobConfig(JobBuilderFactory jobBuilderFactory,
                                StepBuilderFactory stepBuilderFactory,
                                @Qualifier("firstDbTransactionManager") DataSourceTransactionManager firstDbTransactionManager) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.firstDbTransactionManager = firstDbTransactionManager;
    }

    /*
     * @method : sampleBatchJob
     * @desc :
     * @param :
     * @return :
     * */
    @Bean
    public Job sampleBatchJob(CommonJobListener commonJobListener,
                              Step sampleBatchStep) {
        return jobBuilderFactory.get("sampleBatchJob")
                .listener(commonJobListener)
                .preventRestart()                       // 재 시작 금지(Job 중지 후 재시작 불가)
                .start(sampleBatchStep)
                .build();
    }

    /*
     * @method : sampleBatchStep
     * @desc :
     * @param :
     * @return :
     * */
    @Bean
    public Step sampleBatchStep(SampleBatchTasklet sampleBatchTasklet) {
        return stepBuilderFactory.get("sampleBatchStep")
                .transactionManager(firstDbTransactionManager)
                .tasklet(sampleBatchTasklet)
                .build();
    }
}
