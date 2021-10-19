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
import org.springframework.beans.factory.annotation.Autowired;
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

    @Override
    public JobRepository createJobRepository() {
        JobRepositoryFactoryBean factoryBean = new JobRepositoryFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setTransactionManager(transactionManager);
        factoryBean.setIsolationLevelForCreate("ISOLATION_READ_COMMITTED");
        factoryBean.setTablePrefix(TABLE_PREFIX);
        try {
            factoryBean.afterPropertiesSet();
            log.info(">>>>>>>>>>>>>>>>>>>>>createJobRepository..................");
            return factoryBean.getObject();
        } catch (Exception e) {
            log.info(">>>>> CreateJobRepository error");
        }
        return null;
    }

    @Override
    public JobLauncher createJobLauncher() throws Exception {
        log.info(">>>>>>>>>>>>>>>>>>>>>jobLauncher..................");
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor()); // launcher를 async로 호출하기 위함
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }

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
        log.info(">>>>>>>>>>>>>>>>>>>>>jobExplorer..................");
        return factoryBean.getObject();
    }

    @Bean
    public JobRegistry jobRegistry() {
        return new MapJobRegistry();
    }

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

    @Bean
    public JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor(JobRegistry jobRegistry) {
        JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
        jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);
        return jobRegistryBeanPostProcessor;
    }
}
