package com.sziov.common.sharding.multidatasource.config;//package com.sziov.demo.utils;

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
@MapperScan(basePackages = "com.sziov.**.sdao", sqlSessionFactoryRef = "businessSqlSessionFactory")
public class DataSource2Config {
    /**
     * mybatis的mapper配置文件
     */
    @Value("${spring.datasource.primary.mapper-locations:classpath*:com/sziov/**/mapper/item/**/*.xml}")
    private String mapperLocations;

    /**
     * mybatis的实体类对应目录
     */
    @Value("${spring.datasource.primary.type-aliases-package:com.sziov.**.po}")
    private String typeAliasesPackage;

    @Bean(name = "businessMasterDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.business-master")
    public DataSource businessMasterDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "businessSlaveDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.business-slave")
    public DataSource businessSlaveDataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * mybatis的实体类对应目录
     */
    @Bean(name = "businessDataSource")
    public DataSource testDataSource() throws SQLException {
        // 配置真实数据源
        Map<String, DataSource> dataSourceMap = new HashMap<>();

        // 配置主库
        dataSourceMap.put("ds_master", businessMasterDataSource());

        // 配置第一个从库
        dataSourceMap.put("ds_slave", businessSlaveDataSource());

        // 配置读写分离规则
        MasterSlaveRuleConfiguration masterSlaveRuleConfig = new MasterSlaveRuleConfiguration();
        masterSlaveRuleConfig.setName("ds_master_slave");
        masterSlaveRuleConfig.setMasterDataSourceName("ds_master");
        masterSlaveRuleConfig.setSlaveDataSourceNames(Arrays.asList("ds_slave"));

        // 获取数据源对象
        DataSource dataSource = MasterSlaveDataSourceFactory.createDataSource(dataSourceMap, masterSlaveRuleConfig, new HashMap<String, Object>(), new Properties());
        return dataSource;
    }

    @Bean(name = "businessSqlSessionFactory")
    public SqlSessionFactory businessSqlSessionFactory(@Qualifier("businessDataSource") DataSource dataSource)
            throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(mapperLocations));
        bean.setTypeAliasesPackage(typeAliasesPackage);
        return bean.getObject();
    }

    @Bean(name = "businessTransactionManager")
    public DataSourceTransactionManager businessTransactionManager(@Qualifier("businessDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name = "businessSqlSessionTemplate")
    public SqlSessionTemplate businessSqlSessionTemplate(
            @Qualifier("businessSqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
