/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.druid.pool.DruidDataSource
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
 *  org.springframework.boot.autoconfigure.quartz.QuartzDataSource
 *  org.springframework.boot.context.properties.ConfigurationProperties
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.PropertySource
 */
package com.github.iappapp.panda.common.task.quartz.config;

import com.alibaba.druid.pool.DruidDataSource;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.quartz.QuartzDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value={"classpath:quartz_cluster.properties"})
@ConditionalOnExpression(value="#{'true'.equals(environment.getProperty('panda.task.cluster-enable','false'))}")
public class TaskClusterConfig {
    @Bean
    @QuartzDataSource
    @ConfigurationProperties(prefix="spring.datasource.dynamic.datasource.qrtz")
    public DataSource quartzDataSource() {
        return new DruidDataSource();
    }
}

