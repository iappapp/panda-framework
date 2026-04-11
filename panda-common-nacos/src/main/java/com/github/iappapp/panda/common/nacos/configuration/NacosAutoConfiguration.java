/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.nacos.api.config.ConfigService
 *  com.alibaba.nacos.api.naming.NamingService
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
 *  org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.github.iappapp.panda.common.nacos.configuration;

import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.naming.NamingService;
import com.github.iappapp.panda.common.nacos.operation.NacosConfigOperation;
import com.github.iappapp.panda.common.nacos.operation.NacosNamingOperation;
import com.github.iappapp.panda.common.nacos.utils.NacosUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(NacosCommonProperties.class)
public class NacosAutoConfiguration {
    @Autowired
    private NacosUtils nacosUtils;

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name={"panda.nacos.enable"}, havingValue="true", matchIfMissing=true)
    public ConfigService nacosConfigService() {
        return this.nacosUtils.getSystemConfigService();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name={"panda.nacos.enable"}, havingValue="true", matchIfMissing=true)
    public NamingService namingService() {
        return this.nacosUtils.getSystemNamingService();
    }

    @Bean
    @ConditionalOnProperty(name={"panda.nacos.enable"}, havingValue="true", matchIfMissing=true)
    public NacosConfigOperation nacosConfigOperation() {
        return new NacosConfigOperation();
    }

    @Bean
    @ConditionalOnProperty(name={"panda.nacos.enable"}, havingValue="true", matchIfMissing=true)
    public NacosNamingOperation nacosNamingOperation() {
        return new NacosNamingOperation();
    }
}

