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
 */
package com.github.iappapp.panda.common.orm.mybatis.config;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.github.pagehelper.PageInterceptor;
import java.util.Properties;
import org.apache.ibatis.plugin.Interceptor;

public interface PandaMybatisConfigInterface {

    default GlobalConfig getGlobalConfig() {
        GlobalConfig globalConfig = new GlobalConfig();
        GlobalConfig.DbConfig dbConfig = new GlobalConfig.DbConfig();
        dbConfig.setIdType(IdType.AUTO);
        dbConfig.setInsertStrategy(FieldStrategy.NOT_NULL);
        dbConfig.setUpdateStrategy(FieldStrategy.NOT_NULL);
        globalConfig.setDbConfig(dbConfig);
        return globalConfig;
    }

    default Interceptor[] getPlugins() {
        return new Interceptor[]{this.getPageHelper()};
    }

    default PageInterceptor getPageHelper() {
        PageInterceptor pageInterceptor = new PageInterceptor();
        Properties properties = new Properties();
        properties.setProperty("pageSizeZero", "false");
        properties.setProperty("reasonable", "false");
        properties.setProperty("supportMethodsArguments", "true");
        properties.setProperty("autoRuntimeDialect", "true");
        pageInterceptor.setProperties(properties);
        return pageInterceptor;
    }

    default MybatisSqlSessionFactoryBean setSqlSessionFactoryBean(MybatisSqlSessionFactoryBean sqlSessionFactoryBean) {
        return sqlSessionFactoryBean;
    }
}

