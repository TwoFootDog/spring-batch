/*
 * @file : kr.co.kgc.point.batch.common.config.PosDBConfig.java
 * @desc : POS DB 접속정보 설정파일
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
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "kr/co/kgc/point/batch/domain/pos/mapper", sqlSessionFactoryRef = "posSqlSessionFactory")
@EnableTransactionManagement
public class PosDBConfig {

    private final ApplicationContext applicationContext;


    public PosDBConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /*
     * @method : dataSource
     * @desc : Pos DB에 접근 가능한 DataSource Bean 등록
     * @param :
     * @return :
     * */
    @Bean("posDataSource")
    @ConfigurationProperties("spring.datasource.pos")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    /*
     * @method : sqlSessionFactory
     * @desc : Pos DB 용 sqlSessionFactory Bean 등록. Mapper.xml 은 resources/mapper/pos 하단에 있는 xml 파일 참고
     * @param :
     * @return :
     * */
    @Bean(name = "posSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(
            @Qualifier("posDataSource") DataSource dataSource) throws Exception {
        final SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        factoryBean.setDataSource(dataSource);
        factoryBean.setMapperLocations(resolver.getResources("classpath:mapper/pos/*.xml"));
        return factoryBean.getObject();
    }

    /*
     * @method : dataSourceTransactionManager
     * @desc : Pos DB 용 TransactionManager Bean 등록
     * @param :
     * @return :
     * */
    @Bean(name = "posTransactionManager")
    public DataSourceTransactionManager dataSourceTransactionManager(@Qualifier("posDataSource") DataSource dataSource) {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource);
        return transactionManager;
    }
}
