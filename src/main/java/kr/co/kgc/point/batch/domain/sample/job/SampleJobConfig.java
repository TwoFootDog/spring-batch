package kr.co.kgc.point.batch.domain.sample.job;

import kr.co.kgc.point.batch.domain.common.listener.CommJobListener;
import kr.co.kgc.point.batch.domain.sample.tasklet.SampleEaiTasklet;
import kr.co.kgc.point.batch.domain.sample.tasklet.SampleEaiTasklet2;
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
public class SampleJobConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSourceTransactionManager transactionManager;

    public SampleJobConfig(JobBuilderFactory jobBuilderFactory,
                           StepBuilderFactory stepBuilderFactory,
                           @Qualifier("posTransactionManager") DataSourceTransactionManager transactionManager) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.transactionManager = transactionManager;
    }

    @Bean
    public Job sampleJob(CommJobListener commJobListener) {
        return jobBuilderFactory.get("sampleJob")
                .listener(commJobListener)
                .preventRestart()           // 재 시작 금지(Job 중지 후 재시작 불가)
                .start(sampleStep1())       // DB synchronization 대상(Source table) 전체 건수 및 SEQ 시작/종료값 조회
                .next(sampleStep2())        // SEQ 시작/종료값 내에서 대상(Source table) 1건씩 조회 후,
                                            // Target table에 Insert & Source table에 처리 결과 update
                    .on("EXECUTING") // Step의 결과가 EXECUTUING 이면
                    .to(sampleStep2())      // Step 재실행
                    .on("*")         // Step의 결과가 그 외인 경우(COMPLETED, FAILED 등)
                    .end()                  //  Step 처리 종료
                .end()
                .build();
    }

    @Bean
    public Step sampleStep1() {
        return stepBuilderFactory.get("sampleStep1")
                .transactionManager(transactionManager)
                .tasklet(sampleEaiTasklet())
                .build();
    }

    @Bean
    public Step sampleStep2() {
        return stepBuilderFactory.get("sampleStep2")
                .transactionManager(transactionManager)
                .tasklet(sampleEaiTasklet2())
                .build();
    }

    @Bean
    public Tasklet sampleEaiTasklet() {
        return new SampleEaiTasklet();
    }

    @Bean
    public Tasklet sampleEaiTasklet2() {
        return new SampleEaiTasklet2();
    }
}
