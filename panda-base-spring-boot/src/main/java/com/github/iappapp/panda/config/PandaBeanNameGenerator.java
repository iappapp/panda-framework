/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.context.annotation.AnnotationBeanNameGenerator
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 */
package com.github.iappapp.panda.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public class PandaBeanNameGenerator extends AnnotationBeanNameGenerator {
    @Override
    protected String buildDefaultBeanName(BeanDefinition definition) {
        String beanClassName = definition.getBeanClassName();
        Assert.state((beanClassName != null ? 1 : 0) != 0, "No bean class name set");
        String shortClassName = ClassUtils.getShortName(beanClassName);
        return "panda" + shortClassName;
    }
}

