/*
 * @file : kr.co.kgc.point.batch.domain.sample.job.SampleInterfaceJobConfig.java
 * @desc : RestTemplate을 사용해서 외부 API 호출하는 Job / Step / Tasklet을 정의한 Job Config 클래스
 * @auth :
 * @version : 1.0
 * @history
 * version (tag)     프로젝트명     일자      성명    변경내용
 * -------------    ----------   ------   ------  --------
 *
 * */

package kr.co.kgc.point.batch.domain.sample.job;

import kr.co.kgc.point.batch.domain.common.listener.CommonJobListener;
import kr.co.kgc.point.batch.domain.common.listener.CommonStepListener;
import kr.co.kgc.point.batch.domain.sample.tasklet.SampleInterfaceTasklet;
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
public class SampleInterfaceJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSourceTransactionManager transactionManager;

    public SampleInterfaceJobConfig(JobBuilderFactory jobBuilderFactory,
                                    StepBuilderFactory stepBuilderFactory,
                                    @Qualifier("pointTransactionManager") DataSourceTransactionManager transactionManager) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.transactionManager = transactionManager;
    }

    /*
     * @method : sampleInterfaceJob
     * @desc : 외부 API를 호출하는 Batch Job. sampleInterfaceStep을 수행
     * @param : commonJobListener(공통 job 리스너), sampleInterfaceStep(파일 처리 Step)
     * @return :
     * */
    @Bean
    public Job sampleInterfaceJob(CommonJobListener commonJobListener,
                                  @Qualifier("sampleInterfaceStep") Step sampleInterfaceStep) {
        return jobBuilderFactory.get("sampleInterfaceJob")
                .listener(commonJobListener)
                .preventRestart()           // 재 시작 금지(Job 중지 후 재시작 불가)
                .start(sampleInterfaceStep)
                .build();
    }

    /*
     * @method : sampleInterfaceStep
     * @desc : 외부 API를 호출하는 Batch Step. sampleInterfaceTasklet을 수행
     * @param : commonStepListener(공통 Step 리스너)
     * @return :
     * */
    @Bean
    public Step sampleInterfaceStep(CommonStepListener commonStepListener) {
        return stepBuilderFactory.get("sampleInterfaceStep")
                .listener(commonStepListener)
                .transactionManager(transactionManager)
                .tasklet(sampleInterfaceTasklet())
                .build();
    }

    /*
     * @method : sampleInterfaceTasklet
     * @desc : 외부 API를 호출하는 Tasklet(Tasklet이 복잡한 경우는 별도 클래스로 구현 가능함)
     * @param :
     * @return :
     * */
    @Bean
    public Tasklet sampleInterfaceTasklet() {
        return new SampleInterfaceTasklet();
    }
}
