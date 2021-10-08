package co.kr.kgc.point.batch.job.batch;

//import co.kr.kgc.point.kgcbatch.config.JobRepositoryConfig;

import co.kr.kgc.point.batch.job.tasklet.etc.SamplePosTasklet;
import co.kr.kgc.point.batch.mapper.pos.SamplePosMapper;
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
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

@RequiredArgsConstructor
@Configuration
public class SampleJob2Config {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private static final Logger log = LogManager.getLogger(SampleJob2Config.class);
    private final SamplePosMapper samplePosMapper;

    @Qualifier("posTransactionManager")
    @Autowired
    private DataSourceTransactionManager posTransactionManager;

    @Bean
    public Job sampleJob2() {
        return jobBuilderFactory.get("sampleJob2")
                .start(sampleStep2())
                .build();
    }


    @Bean
    public Step sampleStep2() {
        return stepBuilderFactory.get("sampleStep2")
                .transactionManager(posTransactionManager)
                .tasklet(sampleTasklet2())
                .build();
    }

    @Bean
    public Tasklet sampleTasklet2() {
        return new SamplePosTasklet(samplePosMapper);
    }


/*    @Bean
    public Step sampleStep2() {
        return stepBuilderFactory.get("sampleStep2")
                .tasklet((stepContribution, chunkContext) -> {
                    for (int i = 0; i<100; i++) {
                        try {
                            Thread.sleep(2000);
                            log.info(">>> sampleJob2 Tasklet. sampleStep2......" + i + "second elapsed.");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    return RepeatStatus.FINISHED;
                }).build();
    }*/

}
