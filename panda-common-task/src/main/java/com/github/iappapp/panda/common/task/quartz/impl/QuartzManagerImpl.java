/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.baomidou.mybatisplus.core.toolkit.StringUtils
 *  javax.annotation.PostConstruct
 *  lombok.NonNull
 *  org.quartz.CronExpression
 *  org.quartz.CronScheduleBuilder
 *  org.quartz.CronTrigger
 *  org.quartz.Job
 *  org.quartz.JobBuilder
 *  org.quartz.JobDataMap
 *  org.quartz.JobDetail
 *  org.quartz.JobKey
 *  org.quartz.ScheduleBuilder
 *  org.quartz.Scheduler
 *  org.quartz.SchedulerException
 *  org.quartz.SimpleScheduleBuilder
 *  org.quartz.SimpleTrigger
 *  org.quartz.Trigger
 *  org.quartz.TriggerBuilder
 *  org.quartz.TriggerKey
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.scheduling.quartz.SchedulerFactoryBean
 *  org.springframework.stereotype.Component
 */
package com.github.iappapp.panda.common.task.quartz.impl;

import java.util.Date;
import java.util.Map;
import javax.annotation.PostConstruct;

import com.github.iappapp.panda.common.task.QuartzManager;
import com.github.iappapp.panda.common.task.quartz.model.Task;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.ScheduleBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

public class QuartzManagerImpl implements QuartzManager {
    private static final Logger log = LoggerFactory.getLogger(QuartzManagerImpl.class);
    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;
    protected Scheduler scheduler = null;

    @PostConstruct
    public void init() {
        this.scheduler = this.schedulerFactoryBean.getScheduler();
    }

    @Override
    public void addJob(String jobName, Class<? extends Job> jobClazz, int intervalInSeconds) {
        this.addJob(jobName, jobClazz, null, null, intervalInSeconds);
    }

    @Override
    public void addJob(String jobName, Class<? extends Job> jobClazz, int delayInSeconds, int intervalInSeconds) {
        long time = System.currentTimeMillis() + delayInSeconds * 1000L;
        Date triggerStartTime = new Date(time);
        this.addJob(jobName, jobClazz, null, triggerStartTime, intervalInSeconds);
    }

    @Override
    public void addJob(String jobName, Class<? extends Job> jobClazz, JobDataMap jobDataMap, int intervalInSeconds) {
        this.addJob(jobName, jobClazz, jobDataMap, null, intervalInSeconds);
    }

    @Override
    public void addJob(String jobName, Class<? extends Job> jobClazz, JobDataMap jobDataMap,
                       int delayInSeconds, int intervalInSeconds) {
        long time = System.currentTimeMillis() + delayInSeconds * 1000L;
        Date triggerStartTime = new Date(time);
        this.addJob(jobName, jobClazz, jobDataMap, triggerStartTime, intervalInSeconds);
    }

    @Override
    public void addJob(String jobName, Class<? extends Job> jobClazz, String cronExpression) {
        this.addJob(jobName, jobClazz, null, cronExpression);
    }

    @Override
    public void addJob(String jobName, Class<? extends Job> jobClazz, JobDataMap jobDataMap, String cronExpression) {
        try {
            if (!this.scheduler.checkExists(JobKey.jobKey(jobName))) {
                JobDetail jobDetail = this.buildJobDetail(jobName, jobClazz, jobDataMap);
                Trigger trigger = TriggerBuilder.newTrigger()
                        .withIdentity(TriggerKey.triggerKey(jobName))
                        .forJob(jobDetail).startNow().withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                        .build();
                this.scheduler.scheduleJob(jobDetail, trigger);
                if (!this.scheduler.isShutdown()) {
                    this.scheduler.start();
                }
            }
        }
        catch (Exception e) {
            log.error("add job {} failed", jobName);
        }
        log.info("add job {} success", jobName);
    }

    @Override
    public void removeJob(String jobName) {
        try {
            JobKey jobKey = JobKey.jobKey(jobName);
            if (!this.scheduler.checkExists(jobKey)) {
                return;
            }
            TriggerKey triggerKey = TriggerKey.triggerKey(jobName);
            this.scheduler.pauseTrigger(triggerKey);
            this.scheduler.unscheduleJob(triggerKey);
            this.scheduler.deleteJob(jobKey);
        }
        catch (Exception e) {
            log.info("remove job {} failed", jobName);
        }
        log.info("remove job {} success", jobName);
    }

    private void addJob(String jobName, Class<? extends Job> jobClazz, JobDataMap jobDataMap,
                        Date startTime, int intervalInSeconds) {
        try {
            if (!this.scheduler.checkExists(JobKey.jobKey(jobName))) {
                JobDetail jobDetail = this.buildJobDetail(jobName, jobClazz, jobDataMap);
                TriggerBuilder triggerBuilder = TriggerBuilder.newTrigger()
                        .withIdentity(TriggerKey.triggerKey(jobName))
                        .forJob(jobDetail)
                        .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                                .withIntervalInSeconds(intervalInSeconds)
                                .repeatForever());
                if (null == startTime) {
                    triggerBuilder.startNow();
                } else {
                    triggerBuilder.startAt(startTime);
                }
                Trigger trigger = triggerBuilder.build();
                this.scheduler.scheduleJob(jobDetail, trigger);
                if (!this.scheduler.isShutdown()) {
                    this.scheduler.start();
                }
            }
        }
        catch (Exception e) {
            log.error("add job {} failed", jobName);
        }
        log.info("add job {} success", jobName);
    }

    @Override
    public boolean updateJob(String jobName, String cronExpression) {
        try {
            JobKey jobKey = JobKey.jobKey(jobName);
            if (!this.scheduler.checkExists(jobKey)) {
                log.warn("the job to be update not exist.");
                return true;
            }
            TriggerKey triggerKey = TriggerKey.triggerKey(jobName);
            CronTrigger trigger = (CronTrigger)this.scheduler.getTrigger(triggerKey);
            if (trigger != null && !trigger.getCronExpression().equalsIgnoreCase(cronExpression)) {
                CronTrigger newTrigger = TriggerBuilder.newTrigger()
                        .withIdentity(triggerKey)
                        .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                        .startNow().build();
                this.scheduler.rescheduleJob(triggerKey, newTrigger);
            }
            log.info("update job {} success", jobName);
            return true;
        }
        catch (Exception e) {
            log.error("update job exception", e);
            return false;
        }
    }

    @Override
    public boolean updateJob(String jobName, int delayInSeconds, int intervalInSeconds) {
        try {
            JobKey jobKey = JobKey.jobKey(jobName);
            if (!this.scheduler.checkExists(jobKey)) {
                log.warn("the job to be update not exist.");
                return true;
            }
            TriggerKey triggerKey = TriggerKey.triggerKey(jobName);
            SimpleTrigger trigger = (SimpleTrigger)this.scheduler.getTrigger(triggerKey);
            if (trigger != null && trigger.getRepeatInterval() != (long)intervalInSeconds) {
                long time = System.currentTimeMillis() + (long)(delayInSeconds * 1000);
                Date triggerStartTime = new Date(time);
                SimpleTrigger newTrigger = TriggerBuilder.newTrigger()
                        .withIdentity(triggerKey)
                        .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(intervalInSeconds).repeatForever())
                        .startAt(triggerStartTime).build();
                this.scheduler.rescheduleJob(triggerKey, newTrigger);
            }
            log.info("update job {} success", jobName);
            return true;
        }
        catch (Exception e) {
            log.error("update job exception", e);
            return false;
        }
    }

    @Override
    @NonNull
    public boolean addJob(@NonNull Task task) {
        if (task == null) {
            throw new NullPointerException("task is marked non-null but is null");
        }
        try {
            if (task == null) {
                return false;
            }
            if (!CronExpression.isValidExpression(task.getCron())) {
                log.error("panda QuartzManager addJob is false,cron param is error,task is {},cron={}",
                        task.getName(), task.getCron());
                return false;
            }
            Class<?> jobClass = null;
            if (task.getJobClazz() != null) {
                jobClass = task.getJobClazz();
            } else {
                if (StringUtils.isEmpty(task.getJobClass())) {
                    log.error("panda QuartzManager addJob is false,jobClass is null,task is {}", task.getName());
                    return false;
                }
                jobClass = Class.forName(task.getJobClass());
            }
            JobKey jobKey = task.getJobKey();
            if (this.scheduler.checkExists(jobKey)) {
                log.info("{} job has exist, class is {}", jobKey, jobClass);
                return true;
            }
            JobDataMap jobDataMap = this.getJobDataMap(task.getJobDataMap());
            JobDetail jobDetail = this.getJobDetail(jobKey, task.getDesc(), jobDataMap, (Class<? extends Job>) jobClass);
            Trigger trigger = this.getTrigger(jobKey, task.getDesc(), jobDataMap, task.getCron());
            this.scheduler.scheduleJob(jobDetail, trigger);
            return true;
        }
        catch (ClassNotFoundException ce) {
            log.error("panda QuartzManager addJob is false,task is {},ClassNotFound", task.getName(), ce);
        }
        catch (SchedulerException se) {
            log.error("panda QuartzManager addJob is false,task is {},Scheduler", task.getName(), se);
        }
        catch (Exception e) {
            log.error("panda QuartzManager addJob is false,task is {}", task.getName(), e);
        }
        return false;
    }

    @Override
    @NonNull
    public boolean pauseJob(@NonNull JobKey jobkey) {
        if (jobkey == null) {
            throw new NullPointerException("jobkey is marked non-null but is null");
        }
        try {
            this.scheduler.pauseJob(jobkey);
            return true;
        }
        catch (SchedulerException schedulerException) {
        }
        catch (Exception exception) {
            // empty catch block
        }
        return false;
    }

    @Override
    @NonNull
    public boolean pauseJob(@NonNull String jobName, @NonNull String jobGroupName) {
        if (jobName == null) {
            throw new NullPointerException("jobName is marked non-null but is null");
        }
        if (jobGroupName == null) {
            throw new NullPointerException("jobGroupName is marked non-null but is null");
        }
        return this.pauseJob(JobKey.jobKey(jobName, jobGroupName));
    }

    @Override
    @NonNull
    public boolean resumeJob(@NonNull JobKey jobKey) {
        if (jobKey == null) {
            throw new NullPointerException("jobKey is marked non-null but is null");
        }
        try {
            this.scheduler.resumeJob(jobKey);
            return true;
        }
        catch (SchedulerException schedulerException) {
        }
        catch (Exception exception) {
            // empty catch block
        }
        return false;
    }

    @Override
    @NonNull
    public boolean resumeJob(@NonNull String jobName, @NonNull String jobGroupName) {
        if (jobName == null) {
            throw new NullPointerException("jobName is marked non-null but is null");
        }
        if (jobGroupName == null) {
            throw new NullPointerException("jobGroupName is marked non-null but is null");
        }
        return this.resumeJob(JobKey.jobKey(jobName, jobGroupName));
    }

    @Override
    @NonNull
    public boolean deleteJob(@NonNull JobKey jobKey) {
        if (jobKey == null) {
            throw new NullPointerException("jobKey is marked non-null but is null");
        }
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(jobKey.getName(), jobKey.getGroup());
            this.scheduler.pauseTrigger(triggerKey);
            this.scheduler.unscheduleJob(triggerKey);
            this.scheduler.deleteJob(jobKey);
            return true;
        }
        catch (SchedulerException schedulerException) {
        }
        catch (Exception exception) {
            // empty catch block
        }
        return false;
    }

    @Override
    @NonNull
    public boolean deleteJob(@NonNull String jobName, @NonNull String jobGroupName) {
        if (jobName == null) {
            throw new NullPointerException("jobName is marked non-null but is null");
        }
        if (jobGroupName == null) {
            throw new NullPointerException("jobGroupName is marked non-null but is null");
        }
        return this.deleteJob(JobKey.jobKey(jobName, jobGroupName));
    }

    @Override
    @NonNull
    public boolean modifyJobCron(@NonNull Task task) {
        if (task == null) {
            throw new NullPointerException("task is marked non-null but is null");
        }
        String cron = task.getCron();
        if (!CronExpression.isValidExpression(cron)) {
            return false;
        }
        TriggerKey triggerKey = new TriggerKey(task.getName(), task.getGroup());
        try {
            CronTrigger cronTrigger = (CronTrigger)this.scheduler.getTrigger(triggerKey);
            JobDataMap jobDataMap = this.getJobDataMap(task.getJobDataMap());
            if (!cronTrigger.getCronExpression().equalsIgnoreCase(cron)) {
                CronTrigger trigger = TriggerBuilder.newTrigger()
                        .withIdentity(triggerKey)
                        .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                        .usingJobData(jobDataMap).build();
                this.scheduler.rescheduleJob(triggerKey, trigger);
            }
        }
        catch (SchedulerException e) {
            log.error("printStackTrace", e);
            return false;
        }
        return true;
    }

    private JobDetail buildJobDetail(String jobName, Class<? extends Job> jobClazz, JobDataMap jobDataMap) {
        JobBuilder jobBuilder = JobBuilder.newJob(jobClazz).withIdentity(jobName);
        if (null != jobDataMap) {
            jobBuilder.setJobData(jobDataMap);
        }
        return jobBuilder.build();
    }

    public JobDetail getJobDetail(JobKey jobKey, String desc, JobDataMap jobDataMap, Class<? extends Job> jobClass) {
        return JobBuilder.newJob(jobClass)
                .withIdentity(jobKey)
                .withDescription(desc)
                .setJobData(jobDataMap)
                .usingJobData(jobDataMap)
                .requestRecovery()
                .storeDurably().build();
    }

    protected Trigger getTrigger(JobKey jobKey, String description, JobDataMap jobDataMap, String cronExpression) {
        return TriggerBuilder.newTrigger()
                .withIdentity(jobKey.getName(), jobKey.getGroup())
                .withDescription(description)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .usingJobData(jobDataMap).build();
    }

    protected JobDataMap getJobDataMap(Map<?, ?> map) {
        return map == null ? new JobDataMap() : new JobDataMap(map);
    }
}

