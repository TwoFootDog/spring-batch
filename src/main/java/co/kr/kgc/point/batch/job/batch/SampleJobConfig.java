package co.kr.kgc.point.batch.job.batch;

//import co.kr.kgc.point.kgcbatch.config.JobRepositoryConfig;
import co.kr.kgc.point.batch.mapper.SampleMapper;
import co.kr.kgc.point.batch.job.tasklet.SampleTasklet;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@RequiredArgsConstructor
@Configuration
public class SampleJobConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final SampleMapper sampleMapper;

    private static final Logger logger = LogManager.getLogger(SampleJobConfig.class);

    @Bean
    @Primary
    public Job sampleJob() {
        return jobBuilderFactory.get("sampleJob")
                .start(sampleStep1())
                .build();
    }

    @Bean
    public Step sampleStep1() {
        return stepBuilderFactory.get("sampleStep1")
                .tasklet(sampleTasklet1())
                .build();
    }

    @Bean
    public Tasklet sampleTasklet1() {
        return new SampleTasklet(sampleMapper);
    }
}
