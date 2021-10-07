package co.kr.kgc.point.batch.job.batch;

import co.kr.kgc.point.batch.job.Writer.SampleWriter;
import co.kr.kgc.point.batch.mapper.point.SampleMapper;
import co.kr.kgc.point.batch.mapper.pos.SamplePosMapper;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.mybatis.spring.batch.builder.MyBatisPagingItemReaderBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class SampleJob3Config {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final SqlSessionFactory sqlSessionFactory;

    private final SampleMapper sampleMapper;

    private static final Logger log = LogManager.getLogger(SampleJob3Config.class);

    @Bean
    public Job sampleJob3() {
        return jobBuilderFactory.get("sampleJob3")
                .start(sampleStep3())
                .build();
    }

    @Bean
    @JobScope
    public Step sampleStep3() {
        return stepBuilderFactory.get("sampleStep3")
                .<Map<String, Object>, Map<String, Object>>chunk(1000)
                .reader(sampleItemReader3(null))
                .processor(sampleItemProcessor3())
                .writer(sampleItemWriter3(null))
                .build();
    }

    @Bean
    @StepScope
    public MyBatisPagingItemReader<Map<String, Object>> sampleItemReader3(@Value("#{jobParameters['requestDate']}") String requestDate) {
        log.info(">>> sampleItemReader3.... requestDate : {}", requestDate);

        for (int i = 0; i<80; i++) {
            try {
                Thread.sleep(1000);
                log.info(">>> sampleItemReader3..." + i + "second elapsed.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return new MyBatisPagingItemReaderBuilder<Map<String, Object>>()
                .sqlSessionFactory(sqlSessionFactory)
                .queryId("co.kr.kgc.point.batch.mapper.point.SampleMapper.getSampleData")
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<Map<String, Object>, Map<String, Object>> sampleItemProcessor3() {
        return new ItemProcessor<Map<String, Object>, Map<String, Object>>() {
            @Override
            public Map<String, Object> process(Map<String, Object> stringObjectMap) throws Exception {
                log.info(">>> process ..... {}", stringObjectMap);
                return stringObjectMap;
            }
        };
    }

    @Bean
    @StepScope
    public SampleWriter sampleItemWriter3(@Value("#{jobParameters['requestDate']}") String requestDate) {
        log.info(">>> sampleItemWriter3...requestDate : {}....", requestDate);
        return new SampleWriter(sampleMapper);
    }

}
