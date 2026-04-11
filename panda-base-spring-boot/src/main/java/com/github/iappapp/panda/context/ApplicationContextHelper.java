/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.dahua.panda.base.core.exception.SystemException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.support.BeanDefinitionBuilder
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 *  org.springframework.beans.factory.support.DefaultListableBeanFactory
 *  org.springframework.context.ApplicationContext
 *  org.springframework.context.ApplicationContextAware
 *  org.springframework.context.ConfigurableApplicationContext
 *  org.springframework.context.annotation.ComponentScan
 *  org.springframework.stereotype.Component
 */
package com.github.iappapp.panda.context;

import com.github.iappapp.panda.exception.BizException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ApplicationContextHelper implements ApplicationContextAware {
    private static final Logger log = LoggerFactory.getLogger(ApplicationContextHelper.class);
    private static volatile ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (ApplicationContextHelper.applicationContext != null) {
            return;
        }
        synchronized (ApplicationContextHelper.class) {
            if (ApplicationContextHelper.applicationContext != null) {
                return;
            }
            ApplicationContextHelper.applicationContext = applicationContext;
            log.info("ApplicationContextHelper init success.");
        }
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static boolean isInitCompleted() {
        return applicationContext != null;
    }

    public static Object getBean(String beanName) {
        ApplicationContextHelper.checkApplicationContext();
        try {
            return applicationContext.getBean(beanName);
        }
        catch (BeansException beanException) {
            log.error("can not get a bean with beanName: {}", beanName, beanException);
            return null;
        }
    }

    private static void checkApplicationContext() {
        if (ApplicationContextHelper.isInitCompleted()) {
            return;
        }
        throw new BizException("500", "ApplicationContextHelper has not initialized");
    }

    public static <T> T getBean(Class<T> clazz) {
        ApplicationContextHelper.checkApplicationContext();
        try {
            return (T) applicationContext.getBean(clazz);
        }
        catch (BeansException beanException) {
            log.error("can not get a bean with class: {}", clazz, beanException);
            return null;
        }
    }

    public static <T> void registerBean(Class<T> clazz, String beanName) {
        ApplicationContextHelper.checkApplicationContext();
        if (applicationContext.containsBean(beanName)) {
            log.warn("The ApplicationContext has contained the bean with beanName: {}", beanName);
            return;
        }
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
        ConfigurableApplicationContext configurableCtx = (ConfigurableApplicationContext)applicationContext;
        BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry)configurableCtx.getBeanFactory();
        beanDefinitionRegistry.registerBeanDefinition(beanName, beanDefinitionBuilder.getRawBeanDefinition());
    }

    public static void removeBean(String beanName) {
        ApplicationContextHelper.checkApplicationContext();
        DefaultListableBeanFactory beanFactory =
                (DefaultListableBeanFactory)applicationContext.getAutowireCapableBeanFactory();
        beanFactory.removeBeanDefinition(beanName);
    }

    public static String getProperty(String key) {
        return applicationContext.getEnvironment().getProperty(key);
    }
}

