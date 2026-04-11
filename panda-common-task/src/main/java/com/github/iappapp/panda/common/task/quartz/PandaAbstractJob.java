/*
 * Decompiled with CFR 0.152.
 *
 * Could not load the following classes:
 *  cn.hutool.core.collection.CollectionUtil
 *  cn.hutool.core.util.StrUtil
 *  com.baomidou.mybatisplus.core.toolkit.StringUtils
 *  com.dahua.panda.base.core.exception.BusinessException
 *  com.dahua.panda.base.spring.context.ApplicationContextHelper
 *  lombok.NonNull
 *  org.quartz.CronExpression
 *  org.quartz.Job
 *  org.quartz.JobKey
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.core.env.Environment
 *  org.springframework.lang.Nullable
 */
package com.github.iappapp.panda.common.task.quartz;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;

import java.util.Map;

import com.github.iappapp.panda.common.task.quartz.model.Task;
import com.github.iappapp.panda.context.ApplicationContextHelper;
import com.github.iappapp.panda.exception.BusinessException;
import com.google.common.base.Objects;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang3.StringUtils;
import org.quartz.CronExpression;
import org.quartz.Job;
import org.quartz.JobKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.core.ClassesKey;
import org.springframework.core.env.Environment;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;

/**
 *
 */
@Getter
public abstract class PandaAbstractJob {
    private static final Logger log = LoggerFactory.getLogger(PandaAbstractJob.class);

    protected static Environment environment;

    protected static String jobGroupName;

    protected static final String TASK_ENV_FIELD = "panda.task.env";

    protected Class<? extends Job> currentClass;

    protected String jobName = this.getClass().getSimpleName();

    protected Map<Object, Object> jobDataMap;

    public Task getTask() {
        try {
            currentClass = (Class<? extends Job>) this.getClass();
        } catch (Exception e) {
            log.info("currentClass is error");
        }
        return getTask(this.getJobCron(), jobDesc(), currentClass, this.getJobDataMapProperty());
    }

    public Task getTask(String cron) {
        try {
            currentClass = (Class<? extends Job>) this.getClass();
        } catch (Exception e) {
            log.info("currentClass is error");
        }
        return getTask(cron, jobDesc(), currentClass, null);
    }

    public Task getTask(String cron, Map<?, ?> jobDataMap) {
        try {
            currentClass = (Class<? extends Job>) this.getClass();
        } catch (Exception e) {
            log.info("currentClass is error");
        }
        return getTask(cron, this.jobDesc(), currentClass, jobDataMap);
    }

    public Task getTask(String cron, Class<? extends Job> curClass, String desc) {
        try {
            currentClass = (Class<? extends Job>) this.getClass();
        } catch (Exception e) {
            log.info("currentClass is error");
        }
        return getTask(cron, desc, currentClass, null);
    }

    public Task getTask(String cron, String desc, Class<? extends Job> curClass, Map<?, ?> jobDataMap) {
        getJobName();
        getJobGroupName();
        if (currentClass == null
                || StringUtils.isEmpty(jobName)
                || StringUtils.isEmpty(jobGroupName)
                || StringUtils.isEmpty(cron)) {
            log.error("getTask param is error,jobName={},jobGroupName={},cron = {},currentClass={}",
                    jobName, jobGroupName, cron, currentClass.getSimpleName());
            throw new BusinessException("405", "build Task param is error");
        }
        return Task.builder().name(getJobName())
                .group(getJobGroupName())
                .desc(desc)
                .jobClazz(curClass)
                .cron(cron)
                .jobDataMap(jobDataMap).build();
    }

    public JobKey getJobKey() {
        getJobName();
        getJobGroupName();
        if (StringUtils.isEmpty(jobName) || StringUtils.isEmpty(jobGroupName)) {
            log.error("getJobKey param is error,jobName={},jobGroupName={}", jobName, jobGroupName);
            throw new BusinessException("405", "build Task param is error");
        }
        return JobKey.jobKey(getJobName(), getJobGroupName());
    }

    public String getJobGroupName() {
        if (StringUtils.isEmpty(jobGroupName)) {
            try {
                if (environment == null) {
                    environment = ApplicationContextHelper.getBean(Environment.class);
                }
                jobGroupName = environment.getProperty("spring.application.name", "unknownGroup");
            } catch (Exception e) {
                log.error("getJobGroupName is error", e);
            }
        }
        return jobGroupName;
    }

    public String getJobCron() {
        String cronConfig = this.cronProperty();

        if (StringUtils.isBlank(cronConfig)) {
            throw new BusinessException("405", "build Task cron param is error");
        }

        String configKey = null;
        String defaultCron = null;

        // 支持 "key:default" 格式
        if (cronConfig.contains(":")) {
            String[] parts = cronConfig.split(":", 2);
            configKey = parts[0];
            defaultCron = parts[1];
        } else if (CronExpression.isValidExpression(cronConfig)) {
            // 直接是合法 cron 表达式，直接返回
            return cronConfig;
        } else {
            configKey = cronConfig;
        }

        // 尝试从配置中心获取值
        String cronValue = null;
        if (StringUtils.isNotBlank(configKey)) {
            try {
                if (environment == null) {
                    environment = ApplicationContextHelper.getBean(Environment.class);
                }
                cronValue = environment.getProperty(configKey);
            } catch (Exception e) {
                log.error("Failed to resolve cron from config key: {}", configKey, e);
            }
        }

        if (StringUtils.isNotBlank(cronValue)) {
            return cronValue;
        }

        if (StringUtils.isBlank(defaultCron)) {
            throw new BusinessException("405", "build Task cron param is error");
        }

        return defaultCron;
    }

    public String getJobName() {
        return this.getClass().getSimpleName();
    }

    public Map<Object, Object> getJobDataMapProperty() {
        jobDataMap = this.jobDataMapProperty();
        try {
            if (CollectionUtil.isNotEmpty(jobDataMap)) {
                for (Object key : jobDataMap.keySet()) {
                    Object o = jobDataMap.get(key);
                    String s = String.valueOf(o);
                    if (!StrUtil.startWith(s, TASK_ENV_FIELD)) {
                        continue;
                    }
                    String property = environment.getProperty(s);
                    jobDataMap.replace(key, property);
                }
            }
        } catch (Exception e) {
            log.error("panda PandaAbstractJob getJobDataMapProperty isValidExpression is error,JobDataMap", e);
        }
        return jobDataMap;
    }

    @NonNull
    protected abstract String cronProperty();

    @Nullable
    protected abstract Map<Object, Object> jobDataMapProperty();

    @NotNull
    protected abstract String jobDesc();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PandaAbstractJob that = (PandaAbstractJob) o;
        return Objects.equal(currentClass, that.currentClass) && Objects.equal(jobName, that.jobName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(currentClass, jobName);
    }
}

