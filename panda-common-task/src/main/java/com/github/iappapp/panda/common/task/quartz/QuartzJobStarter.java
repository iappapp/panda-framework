/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.dahua.panda.base.spring.context.ApplicationContextHelper
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.boot.ApplicationArguments
 *  org.springframework.boot.ApplicationRunner
 *  org.springframework.core.annotation.Order
 *  org.springframework.stereotype.Component
 *  org.springframework.util.CollectionUtils
 */
package com.github.iappapp.panda.common.task.quartz;

import java.util.HashSet;
import java.util.Map;

import com.github.iappapp.panda.common.task.QuartzManager;
import com.github.iappapp.panda.common.task.quartz.service.ExceptionJobRetryService;
import com.github.iappapp.panda.context.ApplicationContextHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.util.CollectionUtils;

@Order(value = Integer.MIN_VALUE)
public class QuartzJobStarter implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(QuartzJobStarter.class);
    @Autowired
    private QuartzManager quartzManager;
    @Autowired
    private ExceptionJobRetryService exceptionJobRetryService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("panda QuartzJobStarter is start");
        HashSet<PandaAbstractJob> errorClass = new HashSet<>();
        try {
            Map<String, Object> beansWithAutoLoadJobMap =
                    ApplicationContextHelper.getApplicationContext().getBeansWithAnnotation(AutoLoadJob.class);
            if (beansWithAutoLoadJobMap.isEmpty()) {
                log.info("panda QuartzJobStarter beansWithAutoLoadJobMap is null,QuartzJobStarter is end");
                return;
            }
            HashSet<PandaAbstractJob> jobs = new HashSet<>();
            for (Map.Entry<String, Object> entry : beansWithAutoLoadJobMap.entrySet()) {
                try {
                    if (!(entry.getValue() instanceof PandaAbstractJob)) {
                        continue;
                    }
                    jobs.add((PandaAbstractJob) entry.getValue());
                }
                catch (Exception e) {
                    log.error("judge PandaAbstractJob type,and trans PandaAbstractJob is error", e);
                }
            }
            if (CollectionUtils.isEmpty(jobs)) {
                log.info("panda QuartzJobStarter get class from extends PandaAbstractJob is null,QuartzJobStarter is end");
                return;
            }
            for (PandaAbstractJob job : jobs) {
                boolean retry = true;
                Class<?> jobClass = job.getClass();
                try {
                    retry = jobClass.getAnnotation(AutoLoadJob.class).retry();
                    boolean taskResult = this.quartzManager.addJob(job.getTask());
                    log.info("panda QuartzJobStarter add task,taskName={},fulfil={},cron={},result={}",
                            jobClass.getSimpleName(), retry, job.getJobCron(), taskResult);
                    if (!retry || taskResult) {
                        continue;
                    }
                    errorClass.add(job);
                }
                catch (Exception e) {
                    log.info("panda QuartzJobStarter add task is error,taskName={}", jobClass.getSimpleName(), e);
                    if (!retry) {
                        continue;
                    }
                    errorClass.add(job);
                }
            }
        }
        catch (Exception e) {
            log.error("panda QuartzJobStarter is error", e);
        }
        if (!CollectionUtils.isEmpty(errorClass)) {
            this.exceptionJobRetryService.addErrorJobClass(errorClass);
            this.exceptionJobRetryService.retryErrorJob();
        }
    }
}

