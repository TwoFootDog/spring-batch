/*
 * @file : com.project.batch.domain.sample.job.SampleDataSyncJobConfig.java
 * @desc : 이기종 DB 간 테이블 데이터 동기화 처리를 건건 Commit을 통해 수행하는 Job / Step / Tasklet을 정의한 Job Config 클래스
 * @auth :
 * @version : 1.0
 * @history
 * version (tag)     프로젝트명     일자      성명    변경내용
 * -------------    ----------   ------   ------  --------
 *
 * */

package com.project.batch.domain.sample.job;

import com.project.batch.domain.sample.tasklet.SampleDataSync2Tasklet;
import com.project.batch.domain.sample.tasklet.SampleDataSyncTasklet;
import com.project.batch.domain.common.listener.CommonJobListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

@Configuration
public class SampleDataSyncJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSourceTransactionManager firstDbTransactionManager;

    public SampleDataSyncJobConfig(JobBuilderFactory jobBuilderFactory,
                                   StepBuilderFactory stepBuilderFactory,
                                   @Qualifier("firstDbTransactionManager") DataSourceTransactionManager firstDbTransactionManager) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.firstDbTransactionManager = firstDbTransactionManager;
    }

    /*
     * @method : sampleDataSyncJob
     * @desc : 이기종 DB 간 테이블 데이터 동기화 처리를 건건 commit으로 수행하는 Batch Job. sampleDataSyncStep을 통해 수행
     * @param :
     * @return :
     * */
    @Bean
    public Job sampleDataSyncJob(CommonJobListener commonJobListener,
                                      Step sampleDataSyncStep,
                                      Step sampleDataSync2Step) {
        return jobBuilderFactory.get("sampleDataSyncJob")
                .listener(commonJobListener)
                .preventRestart()                       // 재 시작 금지(Job 중지 후 재시작 불가)
                .start(sampleDataSyncStep)              // 데이터 동기화 대상(Source table) 전체 건수 및 SEQ 시작/종료값 조회
                .next(sampleDataSync2Step)              // SEQ 시작/종료값 내에서 동기화 대상(Source table) 1건씩 조회 후,
                                                        // Target table에 Insert & Source table에 처리 결과 update
                    .on("EXECUTING")                    // Step의 결과가 EXECUTUING 이면
                    .to(sampleDataSync2Step)            // Step 재실행
                    .on("*")                            // Step의 결과가 그 외인 경우(COMPLETED, FAILED 등)
                    .end()                              //  Step 처리 종료
                .end()
                .build();
    }

    /*
     * @method : sampleDataSyncStep
     * @desc : 이기종 DB 간 데이터 동기화 진행을 위해 동기화 Source DB의 테이블(SYNC_SOURCE_TABLE) 전체 건수 및 SEQ 시작/종료값 조회
     * @param :
     * @return :
     * */
    @Bean
    public Step sampleDataSyncStep(SampleDataSyncTasklet sampleDataSyncTasklet) {
        return stepBuilderFactory.get("sampleDataSyncStep")
                .transactionManager(firstDbTransactionManager)
                .tasklet(sampleDataSyncTasklet)
                .build();
    }

    /*
     * @method : sampleDataSync2Step
     * @desc : Step1에서 조회한 SEQ 시작/종료값 내에서 동기화 Source DB 테이블(SYNC_SOURCE_TABLE)을 1건씩 조회 후,
     *         동기화 Target DB의 테이블(SYNC_TARGET_TABLE)에 INSERT/UPDATE/DELETE 후 동기화 Source DB 테이블(SYNC_SOURCE_TABLE)에
     * `       동기화 처리 결과 UPDATE
     * @param :
     * @return :
     * */
    @Bean
    public Step sampleDataSync2Step(SampleDataSync2Tasklet sampleDataSync2Tasklet) {
        return stepBuilderFactory.get("sampleDataSync2Step")
                .transactionManager(firstDbTransactionManager)
                .tasklet(sampleDataSync2Tasklet)
                .build();
    }
}
