package com.fulu.game.core.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.fulu.game.core.properties.MultipleDatabaseProperties;
import com.github.pagehelper.PageHelper;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Created by burgl on 2018/4/14.
 */
@Configuration
@EnableConfigurationProperties({MultipleDatabaseProperties.class})
@MapperScan(basePackages = {"com.fulu.game.core.dao"}, sqlSessionFactoryRef = "playAdminDbSqlSessionFactory")
public class PlayAdminDbConfig {

    @Autowired
    private MultipleDatabaseProperties multipleDatabaseProperties;



    @Bean(name = "playAdminDbDataSource")   //声明其为Bean实例
    @Primary
    //在同样的DataSource中，首先使用被标注的DataSource
    public DataSource playAdminDbDataSource() {

        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(multipleDatabaseProperties.getPlayAdminDb().getDriverClassName());
        dataSource.setUrl(multipleDatabaseProperties.getPlayAdminDb().getUrl());
        dataSource.setUsername(multipleDatabaseProperties.getPlayAdminDb().getUsername());
        dataSource.setPassword(multipleDatabaseProperties.getPlayAdminDb().getPassword());
        dataSource.setInitialSize(10);
        dataSource.setMinIdle(10);
        dataSource.setMaxActive(300);
        dataSource.setMaxWait(30000);
        dataSource.setRemoveAbandoned(true);
        dataSource.setRemoveAbandonedTimeout(1800);
        dataSource.setLogAbandoned(false);
        dataSource.setTimeBetweenEvictionRunsMillis(60000);
        dataSource.setMinEvictableIdleTimeMillis(300000);
        dataSource.setValidationQuery("SELECT 1 FROM DUAL");
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(500);
        dataSource.setKeepAlive(true);
        dataSource.setDbType("mysql");
        return dataSource;
    }

    @Bean(name = "playAdminDbSqlSessionFactory")
    @Primary
    public SqlSessionFactory playAdminDbSqlSessionFactory(@Qualifier("playAdminDbDataSource") DataSource playAdminDbDataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(playAdminDbDataSource);

        //分页插件
        PageHelper pageHelper = new PageHelper();
        Properties properties = new Properties();
        properties.setProperty("reasonable", "true");
        properties.setProperty("supportMethodsArguments", "true");
        properties.setProperty("returnPageInfo", "check");
        properties.setProperty("params", "count=countSql");
        pageHelper.setProperties(properties);

        //添加插件
        bean.setPlugins(new Interceptor[]{pageHelper});

        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mybatis/*.xml"));
        bean.getObject().getConfiguration().setMapUnderscoreToCamelCase(true);
        return bean.getObject();
    }

    @Bean(name = "playAdminDbTransactionManager")
    @Primary
    public DataSourceTransactionManager testTransactionManager(@Qualifier("playAdminDbDataSource") DataSource playAdminDbDataSource) {
        return new DataSourceTransactionManager(playAdminDbDataSource);
    }

    @Bean(name = "playAdminDbSqlSessionTemplate")
    @Primary
    public SqlSessionTemplate testSqlSessionTemplate(@Qualifier("playAdminDbSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
    
}
