package co.kr.kgc.point.batch.job.config.point;

import co.kr.kgc.point.batch.job.listener.eai.SampleJobListener;
import co.kr.kgc.point.batch.job.tasklet.eai.SampleEaiTasklet;
import co.kr.kgc.point.batch.job.tasklet.eai.SampleEaiTasklet2;
import co.kr.kgc.point.batch.job.tasklet.point.SampleInterfaceTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

@Configuration
public class SampleJob3Config {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSourceTransactionManager transactionManager;

    public SampleJob3Config(JobBuilderFactory jobBuilderFactory,
                            StepBuilderFactory stepBuilderFactory,
                            @Qualifier("pointTransactionManager") DataSourceTransactionManager transactionManager) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.transactionManager = transactionManager;
    }

    @Bean
    public Job SampleJob3(SampleJobListener sampleJobListener) {
        return jobBuilderFactory.get("sampleJob3")
//                .listener(sampleJobListener)
                .preventRestart()           // 재 시작 금지(Job 중지 후 재시작 불가)
                .start(sampleInterfaceStep())
                .build();
    }

    @Bean
    public Step sampleInterfaceStep() {
        return stepBuilderFactory.get("sampleInterfaceStep")
                .transactionManager(transactionManager)
                .tasklet(sampleInterfaceTasklet())
                .build();
    }


    @Bean
    public Tasklet sampleInterfaceTasklet() {
        return new SampleInterfaceTasklet();
    }
}
