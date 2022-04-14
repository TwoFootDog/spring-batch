/*
 * @file : com.project.batch.domain.sample.job.SampleDataSyncByBulkJobConfig.java
 * @desc : 이기종 DB 간 테이블 데이터 동기화 처리를 대량으로 수행하는 Job / Step / Reader / Processor /
 *         Writer를 정의한 Job Config 클래스
 * @auth :
 * @version : 1.0
 * @history
 * version (tag)     프로젝트명     일자      성명    변경내용
 * -------------    ----------   ------   ------  --------
 *
 * */

package com.project.batch.domain.sample.job;

import com.project.batch.domain.common.listener.CommonJobListener;
import com.project.batch.domain.common.listener.CommonStepListener;
import com.project.batch.domain.sample.writer.SampleDataSyncCompositeWriter;
import com.project.batch.domain.sample.writer.SampleDataSyncSourceWriter;
import com.project.batch.domain.sample.writer.SampleDataSyncTargetWriter;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.mybatis.spring.batch.builder.MyBatisPagingItemReaderBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class SampleDataSyncByBulkJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSourceTransactionManager secondDbTransactionManager;
    private final DataSourceTransactionManager firstDbTransactionManager;
    private final SqlSessionFactory secondDbSqlSessionFactory;
    private final SqlSessionFactory firstDbSqlSessionFactory;
    private static final int CHUNK_SIZE = 2;
    private static final int PAGE_SIZE = 10000;

    public SampleDataSyncByBulkJobConfig(JobBuilderFactory jobBuilderFactory,
                                         StepBuilderFactory stepBuilderFactory,
                                         @Qualifier("secondDbTransactionManager") DataSourceTransactionManager secondDbTransactionManager,
                                         @Qualifier("firstDbTransactionManager") DataSourceTransactionManager firstDbTransactionManager,
                                         @Qualifier("secondDbSqlSessionFactory") SqlSessionFactory secondDbSqlSessionFactory,
                                         @Qualifier("firstDbSqlSessionFactory") SqlSessionFactory firstDbSqlSessionFactory,
                                         JobExplorer jobExplorer) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.secondDbTransactionManager = secondDbTransactionManager;
        this.firstDbTransactionManager = firstDbTransactionManager;
        this.secondDbSqlSessionFactory = secondDbSqlSessionFactory;
        this.firstDbSqlSessionFactory = firstDbSqlSessionFactory;
    }

    /*
     * @method : sampleDataSyncByBulkJob
     * @desc : 이기종 DB 간 테이블 데이터 동기화 처리를 대량으로 수행하는 Batch Job. sampleDataSyncByBulkStep을 통해 수행
     * @param : commonJobListener(공통 job 리스너), sampleInterfaceStep(파일 처리 Step)
     * @return :
     * */
    @Bean
    public Job sampleDataSyncByBulkJob(CommonJobListener commonJobListener,
                          @Qualifier("sampleDataSyncByBulkStep") Step sampleDataSyncByBulkStep)  {
        return jobBuilderFactory.get("sampleDataSyncByBulkJob")
                .listener(commonJobListener)
                .preventRestart()
                .start(sampleDataSyncByBulkStep)
                .build();
    }

    /*
     * @method : sampleDataSyncByBulkStep
     * @desc : 이기종 DB 간 테이블 데이터 동기화 처리를 대량으로 수행하는 Batch Step. sourceItemReader /
     *         sourceItemProcessor / sampleCompositeItemWriter 수행
     * @param : commonStepListener(공통 step 리스너), sampleCompositeItemWriter(복합 Writer)
     * @return :
     * @옵션 값 설명
        skipLimit : 예외 발생 시 예외가 발생한 item을 processor부터 writer까지 1건씩 commit 처리.
                    배치 처리 중 1건씩 commit 하는 건이 skiplimit를 넘어가면 배치 종료
     * */
    @Bean
    public Step sampleDataSyncByBulkStep(CommonStepListener commonStepListener,
                                         MyBatisPagingItemReader sourceItemReader,
                                         ItemProcessor sourceItemProcessor,
                                         SampleDataSyncCompositeWriter sampleDataSyncCompositeWriter) {
        return stepBuilderFactory.get("sampleDataSyncByBulkStep")
                .transactionManager(firstDbTransactionManager)
                .listener(commonStepListener)
                .<Map<String, Object>, Map<String, Object>>chunk(CHUNK_SIZE) // commit-interval 1000
                .faultTolerant()    // skip / retry 기능 사용을 위함
                .skipLimit(1)       // Exception 발생 시 skip 가능 건수.
                .skip(DuplicateKeyException.class)   // pk 중복 에러가 발생할 경우 skip(skip 시 1건씩 건건 처리)
                .processorNonTransactional()    // writer에서 예외 발생하여 1건씩 재 실행 시 processor는 미수행
                .reader(sourceItemReader)
                .processor(sourceItemProcessor)
                .writer(sampleDataSyncCompositeWriter)
                .build();
    }

    /*
     * @method : sourceItemReader
     * @desc : 동기화 SOURCE DB의 테이블(SYNC_SOURCE_TABLE) 데이터를 조회하는 Reader. 한번 조회 시 pageSize만큼 데이터를 조회한다.
     * @param : commonStepListener(공통 step 리스너), sampleCompositeItemWriter(복합 Writer)
     * @return :
     * */
    @Bean
    @StepScope
    public MyBatisPagingItemReader<Map<String, Object>> sourceItemReader(@Value("#{jobParameters['--job.name']}") String jobName,
                                                                         @Value("#{stepExecution}") StepExecution stepExecution) {
        Map<String, Object> parameterValues = new HashMap<>();

        parameterValues.put("jobName", jobName);
        parameterValues.put("stepExecution", stepExecution);

        return new MyBatisPagingItemReaderBuilder<Map<String, Object>>()
                .sqlSessionFactory(firstDbSqlSessionFactory)
                .pageSize(PAGE_SIZE) // 100000 건 씩 조회
                .parameterValues(parameterValues)   // mybatis 쿼리의 parameter로 들어감
                .queryId("com.project.batch.domain.sample.mapper.firstDb.SampleFirstDbMapper.selectSyncSourceDataList")
                .build();
    }

    /*
     * @method : sourceItemProcessor
     * @desc : Reader의 PageSize 만큼 데이터 조회 후 Writer 호출 전 데이터 변경 등이 필요한 경우 Processor 로직 작성 가능
     * @param :
     * @return :
     * */
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

    /*
     * @method : sampleDataSyncCompositeWriter
     * @desc : Reader에서 조회한 데이터를 동기화 Target DB의 테이블(SYNC_TARGET_TABLE)에 DML 처리.
     *         기본적으로 Writer는 1개의 DB에 데이터 입력이 가능하지만,
     *         CompositeItemWriter를 통해 2개의 Writer를 결합시켜 2개의 DB에 데이터를 입력할 수 있음
     *         우선 targetItemWriter를 통해 동기화 Target DB의 테이블(SYNC_TARGET_TABLE)에 데이터를 INSERT/UPDATE/DELETE 시키고,
     *         sourceItemWriter를 통해 동기화 Source DB의 테이블(SYNC_SOURCE_TABLE)에 동기화 결과를 UPDATE 처리
     * @param : commonStepListener(공통 step 리스너), sampleCompositeItemWriter(복합 Writer)
     * @return :
     * */
    @Bean
    @StepScope
    public SampleDataSyncCompositeWriter sampleDataSyncCompositeWriter(SampleDataSyncTargetWriter sampleDataSyncTargetWriter,
                                                                       SampleDataSyncSourceWriter sampleDataSyncSourceWriter,
                                                                       @Value("#{jobParameters[jobName]}") String jobName,
                                                                       @Value("#{stepExecution}") StepExecution stepExecution) {
        SampleDataSyncCompositeWriter sampleDataSyncCompositeWriter = new SampleDataSyncCompositeWriter();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("jobName", jobName);
        parameters.put("stepExecution", stepExecution);
        sampleDataSyncCompositeWriter.setParameterValues(parameters);
        sampleDataSyncCompositeWriter.setDelegates(Arrays.asList(sampleDataSyncTargetWriter, sampleDataSyncSourceWriter));
        return sampleDataSyncCompositeWriter;
    }

    /*
     * @method : sampleDataSyncTargetWriter
     * @desc : 데이터 동기화 Target DB의 테이블(SYNC_TARGET_TABLE)에 데이터를 INSERT 하는 Writer
     * @param : jobName(Batch Job 이름), stepExecution
     * @return :
     * */
    @Bean
    @StepScope
    public SampleDataSyncTargetWriter sampleDataSyncTargetWriter(@Value("#{jobParameters[jobName]}") String jobName,
                                                                 @Value("#{stepExecution}") StepExecution stepExecution) {
        SampleDataSyncTargetWriter sampleDataSyncTargetWriter = new SampleDataSyncTargetWriter();
        sampleDataSyncTargetWriter.setSqlSessionFactory(secondDbSqlSessionFactory);
        sampleDataSyncTargetWriter.setStatementId("com.project.batch.domain.sample.mapper.secondDb.SampleSecondDbMapper.insertSyncTargetData");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("jobName", jobName);
        parameters.put("stepExecution", stepExecution);
        sampleDataSyncTargetWriter.setParameterValues(parameters);

        return sampleDataSyncTargetWriter;
    }

    /*
     * @method : sourceItemWriter
     * @desc : 데이터 동기화 Source DB의 테이블(SYNC_SOURCE_TABLE)에 동기화 결과를 UPDATE 하는 Writer
     * @param : jobName(Batch Job 이름), stepExecution
     * @return :
     * */
    @Bean
    @StepScope
    public SampleDataSyncSourceWriter sampleDataSyncSourceWriter(@Value("#{jobParameters[jobName]}") String jobName,
                                                                 @Value("#{stepExecution}") StepExecution stepExecution) {
        SampleDataSyncSourceWriter sampleDataSyncSourceWriter = new SampleDataSyncSourceWriter();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("jobName", jobName);
        parameters.put("stepExecution", stepExecution);
        sampleDataSyncSourceWriter.setParameterValues(parameters);
        return sampleDataSyncSourceWriter;
    }
}
