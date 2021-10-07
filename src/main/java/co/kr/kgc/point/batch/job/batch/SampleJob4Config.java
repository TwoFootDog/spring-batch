package co.kr.kgc.point.batch.job.batch;

import co.kr.kgc.point.batch.job.Writer.SampleWriter;
import co.kr.kgc.point.batch.job.Writer.SampleWriter2;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.mybatis.spring.batch.builder.MyBatisBatchItemWriterBuilder;
import org.mybatis.spring.batch.builder.MyBatisPagingItemReaderBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Map;

@RequiredArgsConstructor
@Configuration
public class SampleJob4Config {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
/*    private final SampleMapper sampleMapper;
    private final SamplePosMapper samplePosMapper;*/

    @Qualifier("posTransactionManager")
    @Autowired
    private DataSourceTransactionManager posTransactionManager;
    @Qualifier("pointTransactionManager")
    @Autowired
    private DataSourceTransactionManager pointTransactionManager;
    @Qualifier("posSqlSessionFactory")
    @Autowired
    private SqlSessionFactory posSqlSessionFactory;
    @Qualifier("pointSqlSessionFactory")
    @Autowired
    private SqlSessionFactory pointSqlSessionFactory;


    private static final Logger log = LogManager.getLogger(SampleJob4Config.class);

    @Bean
    @Primary
    public Job sampleJob4() {
        return jobBuilderFactory.get("sampleJob4")
                .start(targetDmlStep())
                .build();
    }

    @Bean
    public Step targetDmlStep() {
        return stepBuilderFactory.get("targetDmlStep")
                .transactionManager(pointTransactionManager)
                .<Map<String, Object>, Map<String, Object>>chunk(2000)
                .reader(sourceItemReader())
//                .processor(sourceItemProcessor())
//                .writer(targetItemWriter())
                .writer(compositeItemWriter(pointSqlSessionFactory))
                .build();
    }

    @Bean
    public MyBatisPagingItemReader<Map<String, Object>> sourceItemReader() {
        return new MyBatisPagingItemReaderBuilder<Map<String, Object>>()
                .sqlSessionFactory(posSqlSessionFactory)
                .pageSize(2000) // 2000건씩 조회
                .queryId("co.kr.kgc.point.batch.mapper.pos.SamplePosMapper.selectSamplePosData")
                .build();
    }

    @Bean
    public ItemProcessor<Map<String, Object>, Map<String, Object>> sourceItemProcessor() {
        return new ItemProcessor<Map<String, Object>, Map<String, Object>>() {
            @Override
            public Map<String, Object> process(Map<String, Object> stringObjectMap) throws Exception {
                log.info(">>> process ..... {}", stringObjectMap);
                return stringObjectMap;
            }
        };
    }

/*    @Bean
    public MyBatisBatchItemWriter<Map<String, Object>> targetItemWriter() {
        return new MyBatisBatchItemWriterBuilder<Map<String, Object>>()
                .sqlSessionFactory(pointSqlSessionFactory)
                .statementId("co.kr.kgc.point.batch.mapper.point.SampleMapper.insertSampleData")
                .build();
    }*/

    @Bean
    public MyBatisBatchItemWriter<Map<String, Object>> sourceItemWriter() {
        return new MyBatisBatchItemWriterBuilder<Map<String, Object>>()
                .sqlSessionFactory(posSqlSessionFactory)
                .statementId("co.kr.kgc.point.batch.mapper.pos.SamplePosMapper.updateSamplePosData")
                .build();
    }

/*
    @Bean
    public SampleWriter sourceItemWriter() {
        return new SampleWriter();
    }
*/

    @Bean
    public CompositeItemWriter<Map<String, Object>> compositeItemWriter(@Qualifier("pointSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        CompositeItemWriter<Map<String, Object>> itemWriter = new CompositeItemWriter<>();
//        itemWriter.setDelegates(Arrays.asList(sourceItemWriter(), targetItemWriter()));
        itemWriter.setDelegates(Arrays.asList(sampleItemWriter2(sqlSessionFactory), sampleItemWriter()));
        return itemWriter;
    }

    @Bean
    public SampleWriter sampleItemWriter() {
        return new SampleWriter();
    }

    @Bean
    public SampleWriter2 sampleItemWriter2(@Qualifier("pointSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        SampleWriter2 sampleWriter2 = new SampleWriter2();
        sampleWriter2.setSqlSessionFactory(sqlSessionFactory);
        sampleWriter2.setStatementId("co.kr.kgc.point.batch.mapper.point.SampleMapper.insertSampleData");
        return sampleWriter2;
    }
}
