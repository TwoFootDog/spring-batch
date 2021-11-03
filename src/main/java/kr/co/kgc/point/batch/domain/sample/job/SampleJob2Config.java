package kr.co.kgc.point.batch.domain.sample.job;

import kr.co.kgc.point.batch.domain.common.listener.CommonJobListener;
import kr.co.kgc.point.batch.domain.common.listener.CommonStepListener;
import kr.co.kgc.point.batch.domain.sample.writer.SampleCompositeItemWriter;
import kr.co.kgc.point.batch.domain.sample.writer.SampleWriter;
import kr.co.kgc.point.batch.domain.sample.writer.SampleWriter2;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.mybatis.spring.batch.builder.MyBatisPagingItemReaderBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class SampleJob2Config {
    private static final Logger log = LogManager.getLogger();

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSourceTransactionManager posTransactionManager;
    private final DataSourceTransactionManager pointTransactionManager;
    private final SqlSessionFactory posSqlSessionFactory;
    private final SqlSessionFactory pointSqlSessionFactory;

    public SampleJob2Config(JobBuilderFactory jobBuilderFactory,
                            StepBuilderFactory stepBuilderFactory,
                            @Qualifier("posTransactionManager") DataSourceTransactionManager posTransactionManager,
                            @Qualifier("pointTransactionManager") DataSourceTransactionManager pointTransactionManager,
                            @Qualifier("posSqlSessionFactory") SqlSessionFactory posSqlSessionFactory,
                            @Qualifier("pointSqlSessionFactory") SqlSessionFactory pointSqlSessionFactory,
                            JobExplorer jobExplorer) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.posTransactionManager = posTransactionManager;
        this.pointTransactionManager = pointTransactionManager;
        this.posSqlSessionFactory = posSqlSessionFactory;
        this.pointSqlSessionFactory = pointSqlSessionFactory;
    }

    //    @Primary
    @Bean
    public Job sampleJob2(CommonJobListener commonJobListener,
                          @Qualifier("targetDmlStep") Step targetDmlStep)  {
        return jobBuilderFactory.get("sampleJob2")
                .listener(commonJobListener)
                .preventRestart()
                .start(targetDmlStep)
                .build();
    }

    @Bean
//    @Primary
    @JobScope
    public Step targetDmlStep(CommonStepListener commonStepListener,
                              SampleCompositeItemWriter sampleCompositeItemWriter) {
        return stepBuilderFactory.get("targetDmlStep")
                .transactionManager(posTransactionManager)
                .listener(commonStepListener)
                .<Map<String, Object>, Map<String, Object>>chunk(1000) // commit-interval 1000
                .faultTolerant()    // skip / retry 기능 사용을 위함
                .skipLimit(1)       // Exception 발생 시 skip 가능 건수.
                .skip(DuplicateKeyException.class)   // pk 중복 에러가 발생할 경우 skip(skip 시 1건씩 건건 처리)
                .processorNonTransactional()    // writer에서 예외 발생하여 1건씩 재 실행 시 processor는 미수행
                .reader(sourceItemReader())
                .processor(sourceItemProcessor())
                .writer(sampleCompositeItemWriter)
                .build();
    }
    /* 옵션 값 설명
        skipLimit : 예외 발생 시 예외가 발생한 item을 processor부터 writer까지 1건씩 commit 처리.
                    배치 처리 중 1건씩 commit 하는 건이 skiplimit를 넘어가면 배치 종료
    */

    @Bean
    @StepScope
    public MyBatisPagingItemReader<Map<String, Object>> sourceItemReader() {
        Map<String, Object> parameterValues = new HashMap<>();
//        parameterValues.put("");

        return new MyBatisPagingItemReaderBuilder<Map<String, Object>>()
                .sqlSessionFactory(posSqlSessionFactory)
                .pageSize(100000) // 100000 건 씩 조회
//                .parameterValues()
                .queryId("kr.co.kgc.point.batch.domain.pos.mapper.SamplePosMapper.selectSamplePosData")
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<Map<String, Object>, Map<String, Object>> sourceItemProcessor() {
        return new ItemProcessor<Map<String, Object>, Map<String, Object>>() {
            @Override
            public Map<String, Object> process(Map<String, Object> stringObjectMap) throws Exception {
                return stringObjectMap;
            }
        };
    }

    @Bean
    @StepScope
    public SampleCompositeItemWriter sampleCompositeItemWriter(SampleWriter sampleWriter,
                                                               SampleWriter2 sampleWriter2) {
        SampleCompositeItemWriter myCompositeItemWriter = new SampleCompositeItemWriter();
        myCompositeItemWriter.setDelegates(Arrays.asList(sampleWriter, sampleWriter2));
        return myCompositeItemWriter;
    }

    @Bean
    @StepScope
    public SampleWriter sampleItemWriter(@Value("#{jobParameters[jobName]}") String jobName,
                                         @Value("#{stepExecution}") StepExecution stepExecution) {
        SampleWriter sampleWriter = new SampleWriter();
        sampleWriter.setSqlSessionFactory(pointSqlSessionFactory);
        sampleWriter.setStatementId("kr.co.kgc.point.batch.domain.point.mapper.SamplePointMapper.insertSampleData");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("jobName", jobName);
        parameters.put("stepExecution", stepExecution);
        sampleWriter.setParameterValues(parameters);

        return sampleWriter;
    }

    @Bean
    @StepScope
    public SampleWriter2 sampleItemWriter2(@Value("#{jobParameters[jobName]}") String jobName,
                                           @Value("#{stepExecution}") StepExecution stepExecution) {
        SampleWriter2 sampleWriter2 = new SampleWriter2();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("jobName", jobName);
        parameters.put("stepExecution", stepExecution);
        sampleWriter2.setParameterValues(parameters);
        return sampleWriter2;
    }

    /* Read 당 단일 DML 쿼리인 경우 MybatisBatchItemWriter를 아래와 같이 생성해줘도 됨 */
/*    @Bean
    public MyBatisBatchItemWriter<Map<String, Object>> sourceItemWriter() {
        return new MyBatisBatchItemWriterBuilder<Map<String, Object>>()
                .sqlSessionFactory(posSqlSessionFactory)
                .statementId("co.kr.kgc.point.batch.domain.pos.mapper.SamplePosMapper.updateSamplePosData")
                .build();
    }*/
}
