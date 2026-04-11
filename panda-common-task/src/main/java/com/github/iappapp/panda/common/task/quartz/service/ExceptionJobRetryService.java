/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.thread.ThreadUtil
 *  org.quartz.JobDataMap
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.scheduling.annotation.Async
 *  org.springframework.stereotype.Service
 *  org.springframework.util.CollectionUtils
 */
package com.github.iappapp.panda.common.task.quartz.service;

import cn.hutool.core.thread.ThreadUtil;
import com.github.iappapp.panda.common.task.QuartzManager;
import com.github.iappapp.panda.common.task.quartz.PandaAbstractJob;
import com.github.iappapp.panda.common.task.quartz.config.TaskConfigBuilder;
import org.quartz.JobDataMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ExceptionJobRetryService {
    private static final Logger log = LoggerFactory.getLogger(ExceptionJobRetryService.class);
    private static Set<PandaAbstractJob> errorJobClass = new HashSet<>();
    @Autowired
    private QuartzManager quartzManager;
    @Autowired
    private TaskConfigBuilder taskConfigBuilder;

    @Async
    public void retryErrorJob() {
        log.info("panda fulfilErrorJob is start,errorJob size is {}", errorJobClass.size());
        HashMap<String, Integer> retryCountMap = new HashMap<>();
        while (!CollectionUtils.isEmpty(errorJobClass)) {
            for (PandaAbstractJob clazz : errorJobClass) {
                Integer n;
                Integer n2;
                Integer retryCount = retryCountMap.get(clazz.getClass().getSimpleName());
                if (retryCount == null) {
                    retryCount = 0;
                }
                if (retryCount > this.taskConfigBuilder.getFailJob().getRetryNum()) {
                    return;
                }
                try {
                    boolean taskResult = this.quartzManager.addJob(clazz.getTask());
                    log.info("panda fulfilErrorJob add task,taskName={},retryCount={},result={}",
                            clazz.getClass().getSimpleName(), retryCount, taskResult);
                    if (taskResult) {
                        log.info("panda fulfilErrorJob add task is success,taskName={}",
                                clazz.getClass().getSimpleName());
                        errorJobClass.remove(clazz);
                        retryCountMap.remove(clazz.getClass().getSimpleName());
                        continue;
                    }
                    n2 = retryCount;
                    n = retryCount = retryCount + 1;
                    retryCountMap.put(clazz.getClass().getSimpleName(), retryCount);
                }
                catch (Exception e) {
                    log.info("panda fulfilErrorJob add task is error,taskName={},retryCount={}",
                            clazz.getClass().getSimpleName(), retryCount, e);
                    n2 = retryCount;
                    n = retryCount = retryCount + 1;
                    retryCountMap.put(clazz.getClass().getSimpleName(), retryCount);
                }
            }
            ThreadUtil.sleep((this.taskConfigBuilder.getFailJob().getRetryInterval() * 1000L));
        }
        return;
    }

    public void addErrorJobClass(Set<PandaAbstractJob> errorJobs) {
        errorJobClass.addAll(errorJobs);
    }

    public void addErrorJobClass(PandaAbstractJob errorJob) {
        errorJobClass.add(errorJob);
    }

    public JobDataMap getJobDataMap(Map<?, ?> map) {
        return map == null ? new JobDataMap() : new JobDataMap(map);
    }
}

