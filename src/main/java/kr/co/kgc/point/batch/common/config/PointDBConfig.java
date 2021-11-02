/*
 * @file : kr.co.kgc.point.batch.common.config.PointDBConfig.java
 * @desc : POINT DB 접속정보 설정파일
 * @auth :
 * @version : 1.0
 * @history
 * version (tag)     프로젝트명     일자      성명    변경내용
 * -------------    ----------   ------   ------  --------
 *
 * */

package kr.co.kgc.point.batch.common.config;

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

@Configuration
@MapperScan(basePackages = "kr/co/kgc/point/batch/domain/point/mapper", sqlSessionFactoryRef = "pointSqlSessionFactory")
@EnableTransactionManagement
public class PointDBConfig {

    private final ApplicationContext applicationContext;


    public PointDBConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /*
     * @method : dataSource
     * @desc : Point DB에 접근 가능한 DataSource Bean 등록
     * @param :
     * @return :
     * */
    @Primary
    @Bean("pointDataSource")
    @ConfigurationProperties("spring.datasource.point")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    /*
     * @method : sqlSessionFactory
     * @desc : Point DB 용 sqlSessionFactory Bean 등록. Mapper.xml 은 resources/mapper/point 하단에 있는 xml 파일 참고
     * @param :
     * @return :
     * */
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

    /*
     * @method : dataSourceTransactionManager
     * @desc : Point DB 용 TransactionManager Bean 등록
     * @param :
     * @return :
     * */
    @Primary
    @Bean(name = "pointTransactionManager")
    public DataSourceTransactionManager dataSourceTransactionManager(@Qualifier("pointDataSource") DataSource dataSource) {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource);
        return transactionManager;
    }
}
