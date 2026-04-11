/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.util.StrUtil
 *  com.alibaba.nacos.api.config.ConfigType
 *  com.alibaba.nacos.spring.context.annotation.config.NacosConfigBeanDefinitionRegistrar
 *  com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource
 *  com.alibaba.nacos.spring.context.annotation.config.NacosPropertySources
 *  com.alibaba.nacos.spring.context.event.config.NacosConfigMetadataEvent
 *  com.alibaba.nacos.spring.core.env.AbstractNacosPropertySourceBuilder
 *  com.alibaba.nacos.spring.core.env.NacosPropertySource
 *  com.alibaba.nacos.spring.util.GlobalNacosPropertiesSource
 *  com.alibaba.nacos.spring.util.NacosBeanUtils
 *  com.alibaba.nacos.spring.util.NacosUtils
 *  com.alibaba.nacos.spring.util.config.NacosConfigLoader
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.beans.factory.annotation.AnnotatedBeanDefinition
 *  org.springframework.context.EnvironmentAware
 *  org.springframework.context.annotation.Import
 *  org.springframework.context.annotation.Primary
 *  org.springframework.core.env.Environment
 *  org.springframework.core.env.PropertyResolver
 *  org.springframework.core.type.AnnotationMetadata
 *  org.springframework.stereotype.Component
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.StringUtils
 */
package com.github.iappapp.panda.common.nacos.builder;

import cn.hutool.core.util.StrUtil;
import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.spring.context.annotation.config.NacosConfigBeanDefinitionRegistrar;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySources;
import com.alibaba.nacos.spring.context.event.config.NacosConfigMetadataEvent;
import com.alibaba.nacos.spring.core.env.AbstractNacosPropertySourceBuilder;
import com.alibaba.nacos.spring.util.GlobalNacosPropertiesSource;
import com.alibaba.nacos.spring.util.NacosBeanUtils;
import com.alibaba.nacos.spring.util.NacosUtils;
import com.alibaba.nacos.spring.util.config.NacosConfigLoader;
import com.github.iappapp.panda.common.nacos.utils.Ecc256EncryUtil;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.*;

@Primary
@Component(value = "annotationNacosPropertySourceBuilder")
@Import(value = {NacosConfigBeanDefinitionRegistrar.class})
@ConditionalOnProperty(prefix = "panda.nacos", name = "enable", havingValue = "true", matchIfMissing = false)
public class CustomAnnotationNacosPropertySourceBuilder extends AbstractNacosPropertySourceBuilder<AnnotatedBeanDefinition>
        implements BeanFactoryAware, EnvironmentAware {
    private ClassLoader classLoader;
    private NacosConfigLoader nacosConfigLoader;
    private Properties globalNacosProperties;
    private String appJasyptEncryptorPassword;

    @Override
    public void afterPropertiesSet() {
        this.nacosConfigLoader = new NacosConfigLoader(this.environment);
        this.nacosConfigLoader.setNacosServiceFactory(NacosBeanUtils.getNacosServiceFactoryBean(this.beanFactory));
        this.globalNacosProperties = GlobalNacosPropertiesSource.CONFIG.getMergedGlobalProperties(this.beanFactory);
        String serverAddress = this.globalNacosProperties.getProperty("serverAddr");
        if (StrUtil.isBlank(serverAddress)) {
            serverAddress = this.environment.getProperty("nacos.config.server-addr");
        }
        if (StrUtil.isBlank(serverAddress)) {
            serverAddress = this.environment.getProperty("panda.nacos.server-addr");
        }
        if (StrUtil.isNotBlank(serverAddress)) {
            this.globalNacosProperties.setProperty("serverAddr", serverAddress);
            try {
                Properties bean = this.beanFactory.getBean("globalNacosProperties$config", Properties.class);
                bean.setProperty("serverAddr", serverAddress);
            } catch (Exception e) {
                logger.error("globalNacosProperties is not registry");
            }
        }
        this.appJasyptEncryptorPassword = this.environment.getProperty("jasypt.encryptor.password");
    }

    protected com.alibaba.nacos.spring.core.env.NacosPropertySource doBuild(String beanName,
                                                                            AnnotatedBeanDefinition beanDefinition,
                                                                            Map<String, Object> runtimeAttributes) {
        String name = (String) runtimeAttributes.get("name");
        String dataId = (String) runtimeAttributes.get("dataId");
        String groupId = (String) runtimeAttributes.get("groupId");
        dataId = NacosUtils.readFromEnvironment(dataId, this.environment);
        groupId = NacosUtils.readFromEnvironment(groupId, this.environment);
        ConfigType typeEunm = (ConfigType) runtimeAttributes.get("type");
        String type = ConfigType.UNSET.equals(typeEunm) ? NacosUtils.readFileExtension(dataId) : typeEunm.getType();
        Map<String, Object> nacosPropertiesAttributes = (Map<String, Object>) runtimeAttributes.get("properties");
        Properties nacosProperties =
                NacosUtils.resolveProperties(nacosPropertiesAttributes, this.environment, this.globalNacosProperties);
        String systemServerAddr = nacosProperties.getProperty("serverAddr");
        String userName = nacosProperties.getProperty("username");
        String password = nacosProperties.getProperty("password");
        if (StrUtil.isBlank(userName)) {
            userName = this.environment.getProperty("nacos.config.username");
            password = this.environment.getProperty("nacos.config.password");
        }
        if (StrUtil.isBlank(userName)) {
            userName = this.environment.getProperty("panda.nacos.username");
            password = this.environment.getProperty("panda.nacos.password");
        }
        String ru = Ecc256EncryUtil.getDecryption(userName, this.appJasyptEncryptorPassword);
        String rp = Ecc256EncryUtil.getDecryption(password, this.appJasyptEncryptorPassword);
        nacosProperties.setProperty("username", ru);
        nacosProperties.setProperty("password", rp);
        nacosProperties.setProperty("serverAddr", systemServerAddr);
        String nacosConfig = this.nacosConfigLoader.load(dataId, groupId, nacosProperties);
        if (!StringUtils.hasText(nacosConfig) && logger.isWarnEnabled()) {
            logger.warn(String.format("There is no content for NacosPropertySource from dataId[%s] , groupId[%s] , properties[%s].",
                    dataId, groupId, nacosPropertiesAttributes));
        }
        if (!StringUtils.hasText(name)) {
            name = NacosUtils.buildDefaultPropertySourceName(dataId, groupId, nacosProperties);
        }
        com.alibaba.nacos.spring.core.env.NacosPropertySource nacosPropertySource =
                new com.alibaba.nacos.spring.core.env.NacosPropertySource(dataId, groupId, name, nacosConfig, type);
        nacosPropertySource.setBeanName(beanName);
        String beanClassName = beanDefinition.getBeanClassName();
        if (StringUtils.hasText(beanClassName)) {
            nacosPropertySource.setBeanType(ClassUtils.resolveClassName(beanClassName, this.classLoader));
        }
        nacosPropertySource.setGroupId(groupId);
        nacosPropertySource.setDataId(dataId);
        nacosPropertySource.setProperties(nacosProperties);
        this.initNacosPropertySource(nacosPropertySource, beanDefinition, runtimeAttributes);
        return nacosPropertySource;
    }

    @Override
    protected Map<String, Object>[] resolveRuntimeAttributesArray(AnnotatedBeanDefinition beanDefinition, Properties globalNacosProperties) {
        AnnotationMetadata metadata = beanDefinition.getMetadata();
        Set<String> annotationTypes = metadata.getAnnotationTypes();
        LinkedList<Map<String, Object>> annotationAttributesList = new LinkedList<>();
        for (String annotationType : annotationTypes) {
            annotationAttributesList.addAll(this.getAnnotationAttributesList(metadata, annotationType));
        }
        return annotationAttributesList.toArray(new Map[0]);
    }

    private List<Map<String, Object>> getAnnotationAttributesList(AnnotationMetadata metadata, String annotationType) {
        LinkedList<Map<String, Object>> annotationAttributesList = new LinkedList<>();
        if (NacosPropertySources.class.getName().equals(annotationType)) {
            Map<String, Object> annotationAttributes = metadata.getAnnotationAttributes(annotationType);
            if (annotationAttributes != null) {
                annotationAttributesList.addAll(Collections.singletonList((Map<String, Object>) annotationAttributes.get("value")));
            }
        } else if (NacosPropertySource.class.getName().equals(annotationType)) {
            annotationAttributesList.add(metadata.getAnnotationAttributes(annotationType));
        }
        return annotationAttributesList;
    }

    @Override
    protected void initNacosPropertySource(com.alibaba.nacos.spring.core.env.NacosPropertySource nacosPropertySource,
                                           AnnotatedBeanDefinition beanDefinition, Map<String, Object> annotationAttributes) {
        this.initAttributesMetadata(nacosPropertySource, annotationAttributes);
        this.initAutoRefreshed(nacosPropertySource, annotationAttributes);
        this.initOrigin(nacosPropertySource, beanDefinition);
        this.initOrder(nacosPropertySource, annotationAttributes);
    }

    private void initAttributesMetadata(com.alibaba.nacos.spring.core.env.NacosPropertySource nacosPropertySource,
                                        Map<String, Object> annotationAttributes) {
        nacosPropertySource.setAttributesMetadata(annotationAttributes);
    }

    private void initAutoRefreshed(com.alibaba.nacos.spring.core.env.NacosPropertySource nacosPropertySource,
                                   Map<String, Object> annotationAttributes) {
        boolean autoRefreshed = Boolean.TRUE.equals(annotationAttributes.get("autoRefreshed"));
        nacosPropertySource.setAutoRefreshed(autoRefreshed);
    }

    private void initOrigin(com.alibaba.nacos.spring.core.env.NacosPropertySource nacosPropertySource,
                            AnnotatedBeanDefinition beanDefinition) {
        AnnotationMetadata metadata = beanDefinition.getMetadata();
        nacosPropertySource.setOrigin(metadata.getClassName());
    }

    private void initOrder(com.alibaba.nacos.spring.core.env.NacosPropertySource nacosPropertySource,
                           Map<String, Object> annotationAttributes) {
        boolean first = Boolean.TRUE.equals(annotationAttributes.get("first"));
        String before = (String) annotationAttributes.get("before");
        String after = (String) annotationAttributes.get("after");
        nacosPropertySource.setFirst(first);
        nacosPropertySource.setBefore(before);
        nacosPropertySource.setAfter(after);
    }

    @Override
    protected NacosConfigMetadataEvent createMetaEvent(com.alibaba.nacos.spring.core.env.NacosPropertySource nacosPropertySource,
                                                       AnnotatedBeanDefinition beanDefinition) {
        return new NacosConfigMetadataEvent(beanDefinition.getMetadata());
    }

    @Override
    protected void doInitMetadataEvent(com.alibaba.nacos.spring.core.env.NacosPropertySource nacosPropertySource,
                                       AnnotatedBeanDefinition beanDefinition, NacosConfigMetadataEvent metadataEvent) {
        metadataEvent.setAnnotatedElement(metadataEvent.getAnnotatedElement());
    }
}

