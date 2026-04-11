/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.fastjson.JSON
 *  com.alibaba.fastjson.JSONObject
 *  com.baomidou.dynamic.datasource.provider.AbstractJdbcDataSourceProvider
 *  com.baomidou.dynamic.datasource.provider.YmlDynamicDataSourceProvider
 *  com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty
 *  com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties
 *  com.dahua.panda.common.nacos.operation.NacosConfigOperation
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Value
 *  org.springframework.beans.factory.config.BeanPostProcessor
 *  org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
 *  org.springframework.lang.Nullable
 *  org.springframework.stereotype.Component
 *  org.springframework.util.ReflectionUtils
 */
package com.github.iappapp.panda.common.orm.mybatis.config;

import com.baomidou.dynamic.datasource.provider.AbstractJdbcDataSourceProvider;
import com.baomidou.dynamic.datasource.provider.YmlDynamicDataSourceProvider;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import java.lang.reflect.Field;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

@Configuration
public class DbPropertiesBeanPostProcessor implements BeanPostProcessor, BeanFactoryAware {
    private static final Logger log = LoggerFactory.getLogger(DbPropertiesBeanPostProcessor.class);
    private BeanFactory beanFactory;

    @Value(value="${spring.db.nacos-support:true}")
    private boolean isNacosValueSupport;

    @Value(value="${panda.nacos.enable:true}")
    private boolean pandaNacosEnabled;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Nullable
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (!this.pandaNacosEnabled) {
            return bean;
        }
        if (bean instanceof DataSourceProperties || bean instanceof AbstractJdbcDataSourceProvider || bean instanceof YmlDynamicDataSourceProvider || bean instanceof DataSourceProperty) {
            this.SetDbUser(bean);
        }
        return bean;
    }

    public void SetDbUser(Object bean) {
        try {
            String dbUser = "";
            String dbPasswd = "";
            if (StringUtils.isNotBlank(dbUser) && StringUtils.isNotBlank(dbPasswd)) {
                Class<?> clazz;
                if (bean instanceof DataSourceProperty) {
                    ((DataSourceProperty)bean).setUsername(dbUser);
                    ((DataSourceProperty)bean).setPassword(dbPasswd);
                }
                if (bean instanceof DataSourceProperties) {
                    ((DataSourceProperties)bean).setUsername(dbUser);
                    ((DataSourceProperties)bean).setPassword(dbPasswd);
                }
                if (bean instanceof AbstractJdbcDataSourceProvider) {
                    clazz = AbstractJdbcDataSourceProvider.class;
                    Field usernameField =
                            ReflectionUtils.findField(clazz, "username");
                    ReflectionUtils.makeAccessible(usernameField);
                    ReflectionUtils.setField(usernameField, bean, dbUser);
                    Field passwordField = ReflectionUtils.findField(clazz, "password");
                    ReflectionUtils.makeAccessible(passwordField);
                    ReflectionUtils.setField(passwordField, bean, dbPasswd);
                }
                if (bean instanceof YmlDynamicDataSourceProvider) {
                    clazz = YmlDynamicDataSourceProvider.class;
                    Field propertiesField = ReflectionUtils.findField(clazz, "properties");
                    if (propertiesField == null) {
                        return;
                    }
                    ReflectionUtils.makeAccessible(propertiesField);
                    DynamicDataSourceProperties properties = (DynamicDataSourceProperties) ReflectionUtils.getField(propertiesField, bean);
                    Map<String, DataSourceProperty> dataSourcePropertiesMap = properties.getDatasource();
                    for (Map.Entry<String, DataSourceProperty> item : dataSourcePropertiesMap.entrySet()) {
                        DataSourceProperty dataSourceProperty = item.getValue();
                        dataSourceProperty.setUsername(dbUser);
                        dataSourceProperty.setPassword(dbPasswd);
                    }
                }
            }
        }
        catch (Exception e) {
            log.error("update db userName error!", e);
        }
    }
}

