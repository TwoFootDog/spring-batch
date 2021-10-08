package co.kr.kgc.point.batch.job.batch;

import co.kr.kgc.point.batch.job.tasklet.etc.SamplePosTasklet;
import co.kr.kgc.point.batch.job.tasklet.point.SampleTasklet;
import co.kr.kgc.point.batch.mapper.pos.SamplePosMapper;
import co.kr.kgc.point.batch.mapper.point.SampleMapper;
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

@RequiredArgsConstructor
@Configuration
public class SampleJobConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final SampleMapper sampleMapper;
    private final SamplePosMapper samplePosMapper;

    @Qualifier("posTransactionManager")
    @Autowired
    private DataSourceTransactionManager transactionManager;

    private static final Logger log = LogManager.getLogger(SampleJobConfig.class);

    @Bean
    @Primary
    public Job sampleJob() {
        return jobBuilderFactory.get("sampleJob")
                .start(sampleStep1())
//                .next(sampleEtcStep1())
                .build();
    }

    @Bean
    public Step sampleStep1() {
        return stepBuilderFactory.get("sampleStep1")
                .transactionManager(transactionManager)
                .tasklet(sampleTasklet1())
                .build();
    }

    @Bean
    public Step samplePosStep1() {
        return stepBuilderFactory.get("samplePosStep1")
                .tasklet(samplePosTasklet())
                .build();
    }

    @Bean
    public Tasklet sampleTasklet1() {
        return new SampleTasklet(sampleMapper, samplePosMapper);
    }

    @Bean
    public Tasklet samplePosTasklet() {
        return new SamplePosTasklet(samplePosMapper);
    }
}
