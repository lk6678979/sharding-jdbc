package com.sziov.common.sharding.multidatasource.config;

import io.shardingsphere.api.config.rule.MasterSlaveRuleConfiguration;
import io.shardingsphere.shardingjdbc.api.MasterSlaveDataSourceFactory;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @描述:
 * @公司:
 * @版本: 1.0.0
 * @日期: 2018-12-28 16:27:24
 */
@Configuration
@MapperScan(basePackages = "com.sziov.**.dao", sqlSessionFactoryRef = "primarySqlSessionFactory")
public class PrimaryDataSourceConfig {

    /**
     * mybatis的mapper配置文件
     */
    @Value("${spring.datasource.primary.mapper-locations:classpath*:com/sziov/**/mapper/order/**/*.xml}")
    private String mapperLocations;

    /**
     * mybatis的实体类对应目录
     */
    @Value("${spring.datasource.primary.type-aliases-package:com.sziov.**.po}")
    private String typeAliasesPackage;

    @Bean(name = "primaryMasterDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.primary-master")
    public DataSource primaryMasterDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "primarySlaveDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.primary-slave")
    public DataSource primarySlaveDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "primaryDataSource")
    @Primary
    public DataSource primaryDataSource() throws SQLException {
        // 配置真实数据源
        Map<String, DataSource> dataSourceMap = new HashMap<>();

        // 配置主库
        dataSourceMap.put("ds_master", primaryMasterDataSource());

        // 配置第一个从库
        dataSourceMap.put("ds_slave", primarySlaveDataSource());

        // 配置读写分离规则
        MasterSlaveRuleConfiguration masterSlaveRuleConfig = new MasterSlaveRuleConfiguration();
        masterSlaveRuleConfig.setName("ds_master_slave");
        masterSlaveRuleConfig.setMasterDataSourceName("ds_master");
        masterSlaveRuleConfig.setSlaveDataSourceNames(Arrays.asList("ds_slave"));

        // 获取数据源对象
        DataSource dataSource = MasterSlaveDataSourceFactory.createDataSource(dataSourceMap, masterSlaveRuleConfig, new HashMap<String, Object>(), new Properties());
        return dataSource;
    }

    @Bean(name = "primarySqlSessionFactory")
    @Primary
    public SqlSessionFactory primarySqlSessionFactory(@Qualifier("primaryDataSource") DataSource dataSource)
            throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(mapperLocations));
        bean.setTypeAliasesPackage(typeAliasesPackage);
        return bean.getObject();
    }

    @Bean(name = "primaryTransactionManager")
    @Primary
    public DataSourceTransactionManager primaryTransactionManager(@Qualifier("primaryDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "primarySqlSessionTemplate")
    @Primary
    public SqlSessionTemplate primarySqlSessionTemplate(
            @Qualifier("primarySqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
