package co.kr.kgc.point.batch.job.batch;

//import co.kr.kgc.point.kgcbatch.config.JobRepositoryConfig;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class SampleJob2Config {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private static final Logger log = LogManager.getLogger(SampleJob2Config.class);

    @Bean
    public Job sampleJob2() {
        return jobBuilderFactory.get("sampleJob2")
                .start(sampleStep2())
                .build();
    }

    @Bean
    public Step sampleStep2() {
        return stepBuilderFactory.get("sampleStep2")
                .tasklet((stepContribution, chunkContext) -> {
                    log.info("sampleStep2...................");
                    return RepeatStatus.FINISHED;
                }).build();
    }

}
