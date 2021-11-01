package kr.co.kgc.point.batch.domain.sample.job;

import kr.co.kgc.point.batch.domain.common.listener.CommJobListener;
import kr.co.kgc.point.batch.domain.common.listener.CommStepListener;
import kr.co.kgc.point.batch.domain.sample.tasklet.SampleInterfaceTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

@Configuration
public class SampleInterfaceJobConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSourceTransactionManager transactionManager;

    public SampleInterfaceJobConfig(JobBuilderFactory jobBuilderFactory,
                                    StepBuilderFactory stepBuilderFactory,
                                    @Qualifier("pointTransactionManager") DataSourceTransactionManager transactionManager) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.transactionManager = transactionManager;
    }

    @Bean
    public Job sampleInterfaceJob(CommJobListener commJobListener,
                                  @Qualifier("sampleInterfaceStep") Step sampleInterfaceStep) {
        return jobBuilderFactory.get("sampleInterfaceJob")
                .listener(commJobListener)
                .preventRestart()           // 재 시작 금지(Job 중지 후 재시작 불가)
                .start(sampleInterfaceStep)
                .build();
    }

    @Bean
    public Step sampleInterfaceStep(CommStepListener commStepListener) {
        return stepBuilderFactory.get("sampleInterfaceStep")
                .listener(commStepListener)
                .transactionManager(transactionManager)
                .tasklet(sampleInterfaceTasklet())
                .build();
    }


    @Bean
    public Tasklet sampleInterfaceTasklet() {
        return new SampleInterfaceTasklet();
    }
}
