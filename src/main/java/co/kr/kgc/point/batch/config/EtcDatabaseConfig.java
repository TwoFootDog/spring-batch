package co.kr.kgc.point.batch.config;

import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@RequiredArgsConstructor
@Configuration
@MapperScan(basePackages = "co/kr/kgc/point/batch/mapper/etc")
@EnableTransactionManagement
public class EtcDatabaseConfig {
    private final ApplicationContext applicationContext;

    @Bean("etcDataSource")
    @ConfigurationProperties("spring.datasource.etc")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "etcSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(
            @Qualifier("etcDataSource") DataSource dataSource) throws Exception {
        final SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        factoryBean.setDataSource(dataSource);
        factoryBean.setMapperLocations(resolver.getResources("classpath:mapper/etc/*.xml"));
        return factoryBean.getObject();
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate
            (@Qualifier("etcSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
