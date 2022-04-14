/*
 * @file : com.project.batch.common.config.SecondDBConfig.java
 * @desc : first DB 접속정보 설정파일
 * @auth :
 * @version : 1.0
 * @history
 * version (tag)     프로젝트명     일자      성명    변경내용
 * -------------    ----------   ------   ------  --------
 *
 * */

package com.project.batch.common.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@MapperScan(basePackages = {"com/project/batch/domain/sample/mapper/firstDb"}, sqlSessionFactoryRef = "firstDbSqlSessionFactory")
public class FirstDbConfig {
    private static final Logger log = LogManager.getLogger();
    private final ApplicationContext applicationContext;

    public FirstDbConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    // First DB에 접근 가능한 DataSource Bean 등록
    @Primary
    @Bean(name = "firstDbDataSource")
    @ConfigurationProperties("spring.datasource.first-db")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    // First DB 용 sqlSessionFactory Bean 등록
    @Primary
    @Bean(name = "firstDbSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("firstDbDataSource") DataSource dataSource) throws Exception {
        final SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setConfigLocation(applicationContext.getResource("classpath:mybatis/config/mybatis-config.xml"));
        factoryBean.setMapperLocations(resolveMapperLocations());
//         factoryBean.setMapperLocations(applicationContext.getResources("classpath:mybatis/mapper/*.xml"));
        return factoryBean.getObject();
    }

    // First DB 용 TransactionManager Bean 등록
    @Primary
    @Bean(name = "firstDbTransactionManager")
    public DataSourceTransactionManager dataSourceTransactionManager(@Qualifier("firstDbDataSource") DataSource dataSource) {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource);
        return transactionManager;
    }

    // First DB 용 Mapper xml 리스트를 리턴해주는 메소드
    public Resource[] resolveMapperLocations() {
        ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
        List<String> mapperLocations = new ArrayList<>();
        mapperLocations.add("classpath:mybatis/mapper/sample/firstDb/*.xml");
        List<Resource> resources = new ArrayList<>();
        if (!mapperLocations.isEmpty()) {
            for (String mapperLocation : mapperLocations) {
                try {
                    Resource[] mappers = resourceResolver.getResources(mapperLocation);
                    resources.addAll(Arrays.asList(mappers));
                } catch (IOException e) {
                    log.error("Mybatis resources Get Exception Occur", e);
                }
            }
        }
        return resources.toArray(new Resource[resources.size()]);
    }
}