/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.quartz.spi.JobFactory
 *  org.quartz.spi.TriggerFiredBundle
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.config.AutowireCapableBeanFactory
 *  org.springframework.boot.autoconfigure.quartz.SchedulerFactoryBeanCustomizer
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.core.io.ClassPathResource
 *  org.springframework.core.io.Resource
 *  org.springframework.lang.NonNull
 *  org.springframework.scheduling.quartz.SchedulerFactoryBean
 *  org.springframework.scheduling.quartz.SpringBeanJobFactory
 *  org.springframework.stereotype.Component
 */
package com.github.iappapp.panda.common.task.quartz.config;

import com.github.iappapp.panda.common.task.QuartzManager;
import com.github.iappapp.panda.common.task.quartz.QuartzJobStarter;
import com.github.iappapp.panda.common.task.quartz.impl.QuartzManagerImpl;
import com.github.iappapp.panda.common.task.quartz.service.ExceptionJobRetryService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.quartz.SchedulerFactoryBeanCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.springframework.stereotype.Component;

@Configuration
@Slf4j
@EnableConfigurationProperties(TaskConfigBuilder.class)
@ConditionalOnExpression(value="#{'true'.equals(environment.getProperty('panda.task.enable','false'))}")
public class QuartzConfiguration {

    @Component
    static class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory {
        @Autowired
        private AutowireCapableBeanFactory capableBeanFactory;

        AutowiringSpringBeanJobFactory() {
        }

        @NonNull
        protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
            Object job = super.createJobInstance(bundle);
            this.capableBeanFactory.autowireBean(job);
            return job;
        }
    }

    @Component
    static class PandaSchedulerFactoryBeanCustomizer
    implements SchedulerFactoryBeanCustomizer {
        @Autowired
        private AutowiringSpringBeanJobFactory autowiringSpringBeanJobFactory;

        PandaSchedulerFactoryBeanCustomizer() {
        }

        public void customize(SchedulerFactoryBean schedulerFactoryBean) {
            log.info("panda custom SchedulerFactoryBean");
            ClassPathResource resource = new ClassPathResource("quartz.properties");
            if (resource.exists()) {
                schedulerFactoryBean.setConfigLocation(resource);
            }
            schedulerFactoryBean.setJobFactory(this.autowiringSpringBeanJobFactory);
        }
    }

    @Bean
    public QuartzJobStarter quartzJobStarter() {
        return new QuartzJobStarter();
    }

    @Bean
    public ExceptionJobRetryService exceptionJobFulFilService() {
        return new ExceptionJobRetryService();
    }

    @Bean
    public QuartzManager quartzManager() {
        return new QuartzManagerImpl();
    }
}

