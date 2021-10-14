package co.kr.kgc.point.batch.job.batch;

import co.kr.kgc.point.batch.job.Writer.SampleCompositeItemWriter;
import co.kr.kgc.point.batch.job.Writer.SampleWriter2;
import co.kr.kgc.point.batch.job.Writer.SampleWriter;
import co.kr.kgc.point.batch.job.Writer.SampleWriter3;
import co.kr.kgc.point.batch.job.listener.SampleJobListener;
import co.kr.kgc.point.batch.job.listener.SampleStepListener;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Map;

@Configuration
public class SampleJob4Config {
    private JobBuilderFactory jobBuilderFactory;
    private StepBuilderFactory stepBuilderFactory;
    private DataSourceTransactionManager posTransactionManager;
    private DataSourceTransactionManager pointTransactionManager;
    private SqlSessionFactory posSqlSessionFactory;
    private SqlSessionFactory pointSqlSessionFactory;

    @Autowired
    public SampleJob4Config(JobBuilderFactory jobBuilderFactory,
                            StepBuilderFactory stepBuilderFactory,
                            @Qualifier("posTransactionManager") DataSourceTransactionManager posTransactionManager,
                            @Qualifier("pointTransactionManager") DataSourceTransactionManager pointTransactionManager,
                            @Qualifier("posSqlSessionFactory") SqlSessionFactory posSqlSessionFactory,
                            @Qualifier("pointSqlSessionFactory") SqlSessionFactory pointSqlSessionFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.posTransactionManager = posTransactionManager;
        this.pointTransactionManager = pointTransactionManager;
        this.posSqlSessionFactory = posSqlSessionFactory;
        this.pointSqlSessionFactory = pointSqlSessionFactory;
    }

    private static final Logger log = LogManager.getLogger(SampleJob4Config.class);

    @Bean
    @Primary
    public Job sampleJob4()  {
        return jobBuilderFactory.get("sampleJob4")
                .listener(new SampleJobListener())
                .start(targetDmlStep())
                .build();
    }

    @Bean
    public Step targetDmlStep() {
        return stepBuilderFactory.get("targetDmlStep")
                .transactionManager(posTransactionManager)
                .listener(new SampleStepListener())
                .<Map<String, Object>, Map<String, Object>>chunk(10000) // commit-interval 1000
                .faultTolerant()    // skip / retry 기능 사용을 위함
                .skipLimit(1)       // Exception 발생 시 skip 가능 건수.
                .skip(DuplicateKeyException.class)   // pk 중복 에러가 발생할 경우 skip(skip 시 1건씩 건건 처리)
                .processorNonTransactional()    // writer에서 예외 발생하여 1건씩 재 실행 시 processor는 미수행
                .reader(sourceItemReader())
                .processor(sourceItemProcessor())
                .writer(myCompositeItemWriter())
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
                .pageSize(100000) // 100000 건 씩 조회
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
    public SampleCompositeItemWriter myCompositeItemWriter() {
        SampleCompositeItemWriter myCompositeItemWriter = new SampleCompositeItemWriter();
        myCompositeItemWriter.setDelegates(Arrays.asList(sampleItemWriter(), sampleItemWriter2()));
        return myCompositeItemWriter;
    }

    @Bean
    public SampleWriter sampleItemWriter() {
        SampleWriter sampleWriter = new SampleWriter();
        sampleWriter.setSqlSessionFactory(pointSqlSessionFactory);
        sampleWriter.setStatementId("co.kr.kgc.point.batch.mapper.point.SampleMapper.insertSampleData");
        return sampleWriter;
    }

    @Bean
//    @Transactional(propagation = Propagation.NOT_SUPPORTED, transactionManager ="posTransactionManager")
    public SampleWriter2 sampleItemWriter2() {
        SampleWriter2 sampleWriter2 = new SampleWriter2();
        sampleWriter2.setParameterValues(null);
//        sampleWriter.setSqlSessionFactory(posSqlSessionFactory);
        return sampleWriter2;
    }

/*    @Bean
    public SampleWriter3 sampleItemWriter3() {
        SampleWriter3 sampleWriter3 = new SampleWriter3();
        sampleWriter3.setSqlSessionFactory(posSqlSessionFactory);
        sampleWriter3.setStatementId("co.kr.kgc.point.batch.mapper.pos.SamplePosMapper.updateSamplePosData");
        return sampleWriter3;
    }*/
}
