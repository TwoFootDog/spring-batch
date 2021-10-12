package co.kr.kgc.point.batch.job.batch;

import co.kr.kgc.point.batch.job.Writer.SampleCompositeItemWriter;
import co.kr.kgc.point.batch.job.Writer.SampleWriter;
import co.kr.kgc.point.batch.job.Writer.SampleWriter2;
import co.kr.kgc.point.batch.job.Writer.SampleWriter3;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

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
    public Job sampleJob4()  {
        return jobBuilderFactory.get("sampleJob4")
                .start(targetDmlStep())
                .build();
    }

    @Bean
    public Step targetDmlStep() {
        return stepBuilderFactory.get("targetDmlStep")
                .transactionManager(posTransactionManager)
                .<Map<String, Object>, Map<String, Object>>chunk(10000)
                .faultTolerant()    // skip / retry 기능 사용을 위함
                .skipLimit(1)       // Exception 발생 시 skip 가능 건수.
                .skip(DuplicateKeyException.class)   // pk 중복 에러가 발생할 경우 skip(skip 시 1건씩 건건 처리)
                .processorNonTransactional()    // writer에서 예외 발생하여 1건씩 재 실행 시 processor는 미수행
                .reader(sourceItemReader())
                .processor(sourceItemProcessor())
                .writer(myCompositeItemWriter(pointSqlSessionFactory, posSqlSessionFactory))
                .build();
    }
    /* 옵션 값 설명
        skipLimit : 예외 발생 시 예외가 발생한 item을 processor부터 writer까지 1건씩 commit 처리.
                    배치 처리 중 1건씩 commit 하는 건이 skiplimit를 넘어가면 배치 종료
    */

    @Bean
    public MyBatisPagingItemReader<Map<String, Object>> sourceItemReader() {
        return new MyBatisPagingItemReaderBuilder<Map<String, Object>>()
                .sqlSessionFactory(posSqlSessionFactory)
                .pageSize(10000) // 2000건씩 조회
                .queryId("co.kr.kgc.point.batch.mapper.pos.SamplePosMapper.selectSamplePosData")
                .build();
    }

    @Bean
    public ItemProcessor<Map<String, Object>, Map<String, Object>> sourceItemProcessor() {
        return new ItemProcessor<Map<String, Object>, Map<String, Object>>() {
            @Override
            public Map<String, Object> process(Map<String, Object> stringObjectMap) throws Exception {
/*                int i = 0;
                log.info(i + ">>> process ..... {}", stringObjectMap);
                i++;*/
                return stringObjectMap;
            }
        };
    }

    @Bean
    public MyBatisBatchItemWriter<Map<String, Object>> sourceItemWriter() {
        return new MyBatisBatchItemWriterBuilder<Map<String, Object>>()
                .sqlSessionFactory(posSqlSessionFactory)
                .statementId("co.kr.kgc.point.batch.mapper.pos.SamplePosMapper.updateSamplePosData")
                .build();
    }

    @Bean
    public SampleCompositeItemWriter myCompositeItemWriter(
            @Qualifier("pointSqlSessionFactory") SqlSessionFactory pointSqlSessionFactory,
            @Qualifier("posSqlSessionFactory") SqlSessionFactory posSqlSessionFactory) {
        SampleCompositeItemWriter myCompositeItemWriter = new SampleCompositeItemWriter();
        myCompositeItemWriter.setDelegates(
                Arrays.asList(sampleItemWriter2(pointSqlSessionFactory), sampleItemWriter(posSqlSessionFactory))
        );
        return myCompositeItemWriter;
    }

    @Bean
    public SampleWriter sampleItemWriter(@Qualifier("posSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        SampleWriter sampleWriter = new SampleWriter();
        sampleWriter.setParameterValues(null);
        sampleWriter.setSqlSessionFactory(sqlSessionFactory);
        return new SampleWriter();
    }

    @Bean
    public SampleWriter2 sampleItemWriter2(
            @Qualifier("pointSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        SampleWriter2 sampleWriter2 = new SampleWriter2();
        sampleWriter2.setSqlSessionFactory(sqlSessionFactory);
        sampleWriter2.setStatementId("co.kr.kgc.point.batch.mapper.point.SampleMapper.insertSampleData");
        return sampleWriter2;
    }

    @Bean
    public SampleWriter3 sampleItemWriter3(
            @Qualifier("posSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        SampleWriter3 sampleWriter3 = new SampleWriter3();
        sampleWriter3.setSqlSessionFactory(sqlSessionFactory);
        sampleWriter3.setStatementId("co.kr.kgc.point.batch.mapper.pos.SamplePosMapper.updateSamplePosData");
        return sampleWriter3;
    }
}
