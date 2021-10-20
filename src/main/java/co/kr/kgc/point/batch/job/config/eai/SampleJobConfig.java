package co.kr.kgc.point.batch.job.config.eai;

import co.kr.kgc.point.batch.job.tasklet.eai.SampleEaiTasklet;
import co.kr.kgc.point.batch.job.tasklet.eai.SampleEaiTasklet2;
import co.kr.kgc.point.batch.job.tasklet.pos.SamplePosTasklet;
import co.kr.kgc.point.batch.job.tasklet.point.SamplePointTasklet;
import co.kr.kgc.point.batch.mapper.pos.SamplePosMapper;
import co.kr.kgc.point.batch.mapper.point.SamplePointMapper;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
    @Primary
    public Job sampleJob() {
        return jobBuilderFactory.get("sampleJob")
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
