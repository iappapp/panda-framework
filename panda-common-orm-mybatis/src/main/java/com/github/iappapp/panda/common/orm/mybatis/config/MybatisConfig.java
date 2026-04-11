/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.baomidou.mybatisplus.annotation.FieldStrategy
 *  com.baomidou.mybatisplus.annotation.IdType
 *  com.baomidou.mybatisplus.core.config.GlobalConfig
 *  com.baomidou.mybatisplus.core.config.GlobalConfig$DbConfig
 *  com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean
 *  com.github.pagehelper.PageInterceptor
 *  org.apache.ibatis.plugin.Interceptor
 *  org.apache.ibatis.session.SqlSessionFactory
 *  org.mybatis.spring.boot.autoconfigure.SpringBootVFS
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.beans.factory.annotation.Value
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.DependsOn
 *  org.springframework.context.annotation.Primary
 *  org.springframework.core.io.DefaultResourceLoader
 *  org.springframework.core.io.Resource
 *  org.springframework.core.io.support.PathMatchingResourcePatternResolver
 *  org.springframework.stereotype.Component
 */
package com.github.iappapp.panda.common.orm.mybatis.config;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.github.iappapp.panda.common.orm.mybatis.interceptor.SqlPrintInterceptor;
import com.github.pagehelper.PageInterceptor;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;
import javax.sql.DataSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

@Configuration
public class MybatisConfig {
    private static final Logger log = LoggerFactory.getLogger(MybatisConfig.class);
    @Autowired(required=false)
    private PandaMybatisConfigInterface pandaMybatisConfigInterface;

    @Bean("pandaSqlSessionFactory")
    @ConditionalOnExpression(value="#{'true'.equals(environment.getProperty('panda.mybatis.datasource.enable','true'))}")
    @DependsOn("dataSource")
    @Primary
    public SqlSessionFactory sqlSessionFactory(@Qualifier(value="dataSource") DataSource dataSource,
                                               @Value(value="${mybatis-plus.mapper-locations}") String[] mapperLocations,
                                               @Value(value="${mybatis-plus.config-location:classpath:mybatis/mybatis-config.xml}") String configLocation) throws Exception {
        log.info("SqlSessionFactory init start ..");
        MybatisSqlSessionFactoryBean sqlSessionFactoryBean = new MybatisSqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        Resource[] resources = Stream.of(Optional.ofNullable(mapperLocations).orElse(new String[0]))
                .flatMap(location -> Stream.of(this.getResources(location)))
                .toArray(Resource[]::new);
        sqlSessionFactoryBean.setMapperLocations(resources);
        Resource configResource = new DefaultResourceLoader().getResource(configLocation);
        sqlSessionFactoryBean.setConfigLocation(configResource);
        sqlSessionFactoryBean.setVfs(SpringBootVFS.class);
        if (this.pandaMybatisConfigInterface != null) {
            log.info("pandaMybatisConfigInterface is exist implement bean,Use custom configuration");
            sqlSessionFactoryBean.setGlobalConfig(this.pandaMybatisConfigInterface.getGlobalConfig());
            sqlSessionFactoryBean.setPlugins(this.pandaMybatisConfigInterface.getPlugins());
            sqlSessionFactoryBean = this.pandaMybatisConfigInterface.setSqlSessionFactoryBean(sqlSessionFactoryBean);
        } else {
            log.info("pandaMybatisConfigInterface is not exist implement bean,Use default configuration");
            GlobalConfig globalConfig = new GlobalConfig();
            GlobalConfig.DbConfig dbConfig = new GlobalConfig.DbConfig();
            dbConfig.setIdType(IdType.AUTO);
            dbConfig.setInsertStrategy(FieldStrategy.NOT_NULL);
            dbConfig.setUpdateStrategy(FieldStrategy.NOT_NULL);
            globalConfig.setDbConfig(dbConfig);
            sqlSessionFactoryBean.setGlobalConfig(globalConfig);
            sqlSessionFactoryBean.setPlugins(this.getPlugins());
        }
        return sqlSessionFactoryBean.getObject();
    }

    private Resource[] getResources(String location) {
        try {
            return new PathMatchingResourcePatternResolver().getResources(location);
        }
        catch (IOException e) {
            return new Resource[0];
        }
    }

    private Interceptor[] getPlugins() {
        PageInterceptor pageInterceptor = new PageInterceptor();
        Properties properties = new Properties();
        properties.setProperty("pageSizeZero", "false");
        properties.setProperty("reasonable", "false");
        properties.setProperty("supportMethodsArguments", "true");
        properties.setProperty("autoRuntimeDialect", "true");
        pageInterceptor.setProperties(properties);

        SqlPrintInterceptor sqlPrintInterceptor = new SqlPrintInterceptor();
        return new Interceptor[]{ sqlPrintInterceptor, pageInterceptor};
    }
}

