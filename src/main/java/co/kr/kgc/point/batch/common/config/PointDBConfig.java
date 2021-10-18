package co.kr.kgc.point.batch.common.config;

import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@RequiredArgsConstructor
@Configuration
@MapperScan(basePackages = "co/kr/kgc/point/batch/mapper/point", sqlSessionFactoryRef = "pointSqlSessionFactory")
@EnableTransactionManagement
public class PointDBConfig {
//    private final DataSource dataSource;
    private final ApplicationContext applicationContext;

    @Primary
    @Bean("pointDataSource")
    @ConfigurationProperties("spring.datasource.point")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "pointSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory
            (@Qualifier("pointDataSource") DataSource dataSource) throws Exception {
        final SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        factoryBean.setDataSource(dataSource);
        factoryBean.setMapperLocations(resolver.getResources("classpath:mapper/point/*.xml"));
        return factoryBean.getObject();
    }

    @Primary
    @Bean(name = "pointTransactionManager")
    public DataSourceTransactionManager dataSourceTransactionManager(@Qualifier("pointDataSource") DataSource dataSource) {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource);
        return transactionManager;
    }
}
