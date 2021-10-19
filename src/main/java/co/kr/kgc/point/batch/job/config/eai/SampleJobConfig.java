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
    private static final Logger log = LogManager.getLogger(SampleJobConfig.class);
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
//    private final SamplePointMapper samplePointMapper;
//    private final SamplePosMapper samplePosMapper;
    private final DataSourceTransactionManager transactionManager;


    public SampleJobConfig(JobBuilderFactory jobBuilderFactory,
                           StepBuilderFactory stepBuilderFactory,
//                           SamplePosMapper samplePosMapper,
//                           SamplePointMapper samplePointMapper,
                           @Qualifier("posTransactionManager") DataSourceTransactionManager transactionManager) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
//        this.samplePosMapper = samplePosMapper;
//        this.samplePointMapper = samplePointMapper;
        this.transactionManager = transactionManager;
    }

    @Bean
    @Primary
    public Job sampleJob() {
        return jobBuilderFactory.get("sampleJob")
                .start(sampleStep1())
                .next(sampleStep2())
                    .on("EXECUTING")
                    .to(sampleStep2())
                    .on("*")
                    .end()
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
