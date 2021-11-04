/*
 * @file : kr.co.kgc.point.batch.domain.sample.job.SampleDataSyncByOneJobConfig.java
 * @desc : 이기종 DB 간 테이블 데이터 동기화 처리를 건건 Commit을 통해 수행하는 Job / Step / Tasklet을 정의한 Job Config 클래스
 * @auth :
 * @version : 1.0
 * @history
 * version (tag)     프로젝트명     일자      성명    변경내용
 * -------------    ----------   ------   ------  --------
 *
 * */

package kr.co.kgc.point.batch.domain.sample.job;

import kr.co.kgc.point.batch.domain.common.listener.CommonJobListener;
import kr.co.kgc.point.batch.domain.sample.tasklet.SampleDataSyncTasklet;
import kr.co.kgc.point.batch.domain.sample.tasklet.SampleDataSync2Tasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

@Configuration
public class SampleDataSyncByOneJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSourceTransactionManager posTransactionManager;

    public SampleDataSyncByOneJobConfig(JobBuilderFactory jobBuilderFactory,
                                        StepBuilderFactory stepBuilderFactory,
                                        @Qualifier("posTransactionManager") DataSourceTransactionManager posTransactionManager) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.posTransactionManager = posTransactionManager;
    }

    /*
     * @method : sampleDataSyncByOneJob
     * @desc : 이기종 DB 간 테이블 데이터 동기화 처리를 건건 commit으로 수행하는 Batch Job. sampleDataSyncByBulkStep을 통해 수행
     * @param : commonJobListener(공통 job 리스너), sampleInterfaceStep(파일 처리 Step)
     * @return :
     * */
    @Bean
    public Job sampleDataSyncByOneJob(CommonJobListener commonJobListener) {
        return jobBuilderFactory.get("sampleDataSyncByOneJob")
                .listener(commonJobListener)
                .preventRestart()                        // 재 시작 금지(Job 중지 후 재시작 불가)
                .start(sampleDataSyncByOneStep())       // 데이터 동기화 대상(Source table) 전체 건수 및 SEQ 시작/종료값 조회
                .next(sampleDataSyncByOne2Step())        // SEQ 시작/종료값 내에서 동기화 대상(Source table) 1건씩 조회 후,
                                                         // Target table에 Insert & Source table에 처리 결과 update
                    .on("EXECUTING")              // Step의 결과가 EXECUTUING 이면
                    .to(sampleDataSyncByOne2Step())      // Step 재실행
                    .on("*")                      // Step의 결과가 그 외인 경우(COMPLETED, FAILED 등)
                    .end()                               //  Step 처리 종료
                .end()
                .build();
    }

    /*
     * @method : sampleDataSyncByOneStep
     * @desc : 이기종 DB 간 데이터 동기화 진행을 위해 동기화 Source DB의 테이블(POS_IF_TABLE1) 전체 건수 및 SEQ 시작/종료값 조회
     * @param :
     * @return :
     * */
    @Bean
    public Step sampleDataSyncByOneStep() {
        return stepBuilderFactory.get("sampleDataSyncByOneStep1")
                .transactionManager(posTransactionManager)
                .tasklet(sampleDataSyncTasklet())
                .build();
    }

    /*
     * @method : sampleDataSyncByOne2Step
     * @desc : Step1에서 조회한 SEQ 시작/종료값 내에서 동기화 Source DB 테이블(POS_IF_TABLE1)을 1건씩 조회 후,
     *         동기화 Target DB의 테이블(POINT_TABLE1)에 INSERT 후 동기화 Source DB 테이블(POS_IF_TABLE1)에 동기화 처리 결과 UPDATE
     * @param :
     * @return :
     * */
    @Bean
    public Step sampleDataSyncByOne2Step() {
        return stepBuilderFactory.get("sampleDataSyncByOne2Step")
                .transactionManager(posTransactionManager)
                .tasklet(sampleDataSync2Tasklet())
                .build();
    }

    /*
     * @method : sampleDataSyncTasklet
     * @desc : Source DB 테이블(POS_IF_TABLE1)의 동기화 대상 전체 건수 및 SEQ 시작/종료값 조회하는 Tasklet
     * @param :
     * @return :
     * */
    @Bean
    public Tasklet sampleDataSyncTasklet() {
        return new SampleDataSyncTasklet();
    }

    /*
     * @method : sampleDataSync2Tasklet
     * @desc : 동기화 Target DB의 테이블(POINT_TABLE1)에 데이터 INSERT 후,
     *         동기화 Source DB의 테이블(POS_IF_TABLE1)에 동기화 처리 결과 UPDATE
     * @param :
     * @return :
     * */
    @Bean
    public Tasklet sampleDataSync2Tasklet() {
        return new SampleDataSync2Tasklet();
    }
}
