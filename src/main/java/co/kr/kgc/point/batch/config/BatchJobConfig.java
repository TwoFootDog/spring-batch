package co.kr.kgc.point.batch.config;

import lombok.RequiredArgsConstructor;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/* 스프링 배치 설정파일 */
@Configuration
@RequiredArgsConstructor
public class BatchJobConfig extends DefaultBatchConfigurer {
    private static final Logger log = LogManager.getLogger(BatchJobConfig.class);
    private final PlatformTransactionManager transactionManager;
    private static final String TABLE_PREFIX = "BATCH_";
    private final JobRepository jobRepository;
    private final JobLauncher jobLauncher;
    private final JobExplorer jobExplorer;
    @Qualifier("pointDataSource")
    @Autowired
    private DataSource dataSource;

    /* 수행되는 Job에 대한 정보를 담고 있는 저장소 */
    @Override
    public JobRepository createJobRepository() {
        log.info(">>>>>>>>>>>>>>>>>>>>>createJobRepository..................");
        JobRepositoryFactoryBean factoryBean = new JobRepositoryFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setTransactionManager(transactionManager);
        factoryBean.setIsolationLevelForCreate("ISOLATION_READ_COMMITTED");
        factoryBean.setTablePrefix(TABLE_PREFIX);
        try {
            factoryBean.afterPropertiesSet();
            return factoryBean.getObject();
        } catch (Exception e) {
            log.info("error");
        }
        return null;
    }

    /* 배치 Job을 실행시키는 역할 수행 */
    @Override
    public JobLauncher createJobLauncher() throws Exception {
        log.info(">>>>>>>>>>>>>>>>>>>>>jobLauncher..................");
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor()); // launcher를 async로 호출하기 위함
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }

    /* Repository에 접근하여 배치작업에 대한 정보를 얻을 수 있음 */
    @Override
    public JobExplorer createJobExplorer() {
        JobExplorerFactoryBean factoryBean = new JobExplorerFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setTablePrefix(TABLE_PREFIX);
        try {
            factoryBean.afterPropertiesSet();
            return factoryBean.getObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /* 생성된 Job을 Map 형태로 추가, 삭제 등 수행 */
    @Bean(name = "jobRegistry")
    public JobRegistry jobRegistry() {
        return new MapJobRegistry();
    }

    /* 현재 실행중인 Job의 정보를 활용하여 Job 제어 및 모니터링 */
    @Bean
    public JobOperator jobOperator() throws Exception {
        log.info(">>>>>>>>>>>>>>>>>>>>>job Operator..................");
        SimpleJobOperator jobOperator = new SimpleJobOperator();
        jobOperator.setJobExplorer(jobExplorer);
        jobOperator.setJobLauncher(jobLauncher);
        jobOperator.setJobRepository(jobRepository);
        jobOperator.setJobRegistry(jobRegistry());
        return jobOperator;
    }

    /* JobRegistryBeanPostProcessor는 Bean post-processor으로 Application Context가 올라가면서 bean 등록 시,
       자동으로 JobRegistry에 Job을 등록 시켜준다.*/
    @Bean
    public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
        JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
        jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);
        return jobRegistryBeanPostProcessor;
    }
}