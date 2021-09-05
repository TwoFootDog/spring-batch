//package co.kr.kgc.point.kgcbatch.config;
//
//import lombok.RequiredArgsConstructor;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.springframework.batch.core.configuration.BatchConfigurationException;
//import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
//import org.springframework.batch.core.repository.JobRepository;
//import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.transaction.PlatformTransactionManager;
//
//import javax.sql.DataSource;
//
///* DB가 ORACLE인 경우는 JobRepository를 별도로 만든 후 setIsolationLevelForCreate 설정을 "ISOLATION_READ_COMMITTED 설정을
//* 해주지 않으면 */
//@RequiredArgsConstructor
//@Configuration
//public class JobRepositoryConfig extends DefaultBatchConfigurer {
//    private final DataSource dataSource;
//    private final PlatformTransactionManager transactionManager;
//    private static final Logger logger = LogManager.getLogger(JobRepositoryConfig.class);
//
//    @Override
//    protected JobRepository createJobRepository() throws Exception {
//        logger.info("createJobRepository....");
//        JobRepositoryFactoryBean factoryBean = new JobRepositoryFactoryBean();
//        factoryBean.setDataSource(dataSource);
//        factoryBean.setTransactionManager(transactionManager);
//        factoryBean.setIsolationLevelForCreate("ISOLATION_READ_COMMITTED");
//        factoryBean.setTablePrefix("BATCH_");
//        try {
//            factoryBean.afterPropertiesSet();
//            return factoryBean.getObject();
//        } catch (Exception e) {
//            throw new BatchConfigurationException(e);
//        }
//    }
//}
