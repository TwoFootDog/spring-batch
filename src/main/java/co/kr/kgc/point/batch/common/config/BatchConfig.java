/*
* @file : co.kr.kgc.point.batch.common.config.BatchConfig.java
* @desc : spring batch 에 사용되는 bean을 등록해주는 클래스
* @auth :
* @version : 1.0
* @history
* version (tag)     프로젝트명     일자      성명    변경내용
* -------------    ----------   ------   ------  --------
*
* */

package co.kr.kgc.point.batch.common.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.configuration.support.MapJobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
public class BatchConfig extends DefaultBatchConfigurer {
    private static final Logger log = LogManager.getLogger(BatchConfig.class);
    private static final String TABLE_PREFIX = "BATCH_";
    private final JobRepository jobRepository;
    private final JobLauncher jobLauncher;
    private final JobExplorer jobExplorer;
    private final DataSource dataSource;
    private final DataSourceTransactionManager transactionManager;

    public BatchConfig(JobRepository jobRepository,
                       JobLauncher jobLauncher,
                       JobExplorer jobExplorer,
                       @Qualifier("pointDataSource") DataSource dataSource,
                       @Qualifier("pointTransactionManager") DataSourceTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.jobLauncher = jobLauncher;
        this.jobExplorer = jobExplorer;
        this.dataSource = dataSource;
        this.transactionManager = transactionManager;
    }

    /*
    * @method : createJobRepository
    * @desc : 수행되는 Job 정보를 저장하는 저장소인 JobRepositry Bean 정보를 Customizing(datasource, transactionManager 등)
    * @param :
    * @return : JobRepositry
    * */
    @Override
    public JobRepository createJobRepository() {
        JobRepositoryFactoryBean factoryBean = new JobRepositoryFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setTransactionManager(transactionManager);
        factoryBean.setIsolationLevelForCreate("ISOLATION_READ_COMMITTED");
        factoryBean.setTablePrefix(TABLE_PREFIX);
        try {
            factoryBean.afterPropertiesSet();
            log.info(">>> JobRepository create");
            return factoryBean.getObject();
        } catch (Exception e) {
            log.info(">>> JobRepository error");
        }
        return null;
    }

    /*
     * @method : createJobLauncher
     * @desc : 배치 Job을 실행시키는 JobLauncher Bean 정보를 Customizing 해주는 메소드(jobRepositry, TaskExecutor 등)
     * @param :
     * @return : JobLauncher
     * */
    @Override
    public JobLauncher createJobLauncher() throws Exception {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor()); // launcher를 async로 호출
        jobLauncher.afterPropertiesSet();
        log.info(">>> JobLauncher create");
        return jobLauncher;
    }

    /*
     * @method : createJobExplorer
     * @desc : Repository에 접근하기 위해 사용하는 JobExplorer Bean 정보를 Customizing 해주는 메소드
     *         (datasource, batch job table prefix 등)
     * @param :
     * @return : JobExplorer
     * */
    @Override
    public JobExplorer createJobExplorer() throws Exception {
        JobExplorerFactoryBean factoryBean = new JobExplorerFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setTablePrefix(TABLE_PREFIX);
        try {
            factoryBean.afterPropertiesSet();
            return factoryBean.getObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info(">>> JobExplorer create");
        return factoryBean.getObject();
    }

    /*
     * @method : jobRegistry
     * @desc : 생성된 Job을 Map 형태로 추가, 삭제 등 수행하는 JobRegistry Bean 등록
     * @param :
     * @return : JobRegistry
     * */
    @Bean
    public JobRegistry jobRegistry() {
        return new MapJobRegistry();
    }

    /*
     * @method : jobOperator
     * @desc : Job 제어 및 모니터링 할 수 있는 JobOperator Bean 등록.
     *         등록 시 JobExplorer, JobLauncher, JobRepository, JobRegistry 정보 셋팅
     * @param :
     * @return : JobOperator
     * */
    @Bean
    public JobOperator jobOperator() throws Exception {
        SimpleJobOperator jobOperator = new SimpleJobOperator();
        jobOperator.setJobExplorer(jobExplorer);
        jobOperator.setJobLauncher(jobLauncher);
        jobOperator.setJobRepository(jobRepository);
        jobOperator.setJobRegistry(jobRegistry());
        log.info(">>> JobOperator create");
        return jobOperator;
    }

    /*
     * @method : jobRegistryBeanPostProcessor
     * @desc : Bean post-processor으로 Application Context가 올라가면서 bean 등록 시,
               자동으로 JobRegistry에 Job을 등록
     * @param :
     * @return : JobRegistryBeanPostProcessor
     * */
    @Bean
    public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
        JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
        jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);
        log.info(">>> JobRegistryBeanPostProcessor create");
        return jobRegistryBeanPostProcessor;
    }
}
