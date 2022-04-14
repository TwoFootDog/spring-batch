/*
 * @file : com.project.batch.common.config.SecondDBConfig.java
 * @desc : second DB 접속정보 설정파일
 * @auth :
 * @version : 1.0
 * @history
 * version (tag)     프로젝트명     일자      성명    변경내용
 * -------------    ----------   ------   ------  --------
 *
 * */
package com.project.batch.common.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = {"com/project/batch/domain/sample/mapper/secondDb"}, sqlSessionFactoryRef = "secondDbSqlSessionFactory")
@EnableTransactionManagement
public class SecondDbConfig {

    private final ApplicationContext applicationContext;


    public SecondDbConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /*
     * @method : dataSource
     * @desc : Second DB에 접근 가능한 DataSource Bean 등록
     * @param :
     * @return :
     * */
    @Bean("secondDbDataSource")
    @ConfigurationProperties("spring.datasource.second-db")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    /*
     * @method : sqlSessionFactory
     * @desc : Second DB 용 sqlSessionFactory Bean 등록. Mapper.xml 은 resources/mapper/second 하단에 있는 xml 파일 참고
     * @param :
     * @return :
     * */
    @Bean(name = "secondDbSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(
            @Qualifier("secondDbDataSource") DataSource dataSource) throws Exception {
        final SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        factoryBean.setDataSource(dataSource);
        factoryBean.setConfigLocation(applicationContext.getResource("classpath:mybatis/config/mybatis-config.xml"));
        factoryBean.setMapperLocations(resolver.getResources("classpath:mybatis/mapper/sample/secondDb/*.xml"));
        return factoryBean.getObject();
    }

    /*
     * @method : dataSourceTransactionManager
     * @desc : Second DB 용 TransactionManager Bean 등록
     * @param :
     * @return :
     * */
    @Bean(name = "secondDbTransactionManager")
    public DataSourceTransactionManager dataSourceTransactionManager(@Qualifier("secondDbDataSource") DataSource dataSource) {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource);
        return transactionManager;
    }
}

