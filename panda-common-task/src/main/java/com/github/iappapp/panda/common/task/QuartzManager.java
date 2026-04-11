/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 *  org.quartz.Job
 *  org.quartz.JobDataMap
 *  org.quartz.JobKey
 */
package com.github.iappapp.panda.common.task;

import com.github.iappapp.panda.common.task.quartz.model.Task;
import lombok.NonNull;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobKey;

public interface QuartzManager {
    void addJob(String jobName, Class<? extends Job> jobClass, int intervalInSeconds);

    void addJob(String jobName, Class<? extends Job> jobClazz, int delayInSeconds,
                       int intervalInSeconds);

    void addJob(String jobName, Class<? extends Job> jobClazz, JobDataMap jobDataMap,
                       int intervalInSeconds);

    void addJob(String jobName, Class<? extends Job> jobClazz, JobDataMap jobDataMap,
                       int delayInSeconds, int intervalInSeconds);

    void addJob(String jobName, Class<? extends Job> jobClazz, String cronExpression);

    void addJob(String jobName, Class<? extends Job> jobClazz, JobDataMap jobDataMap,
                       String cronExpression);

    void removeJob(String jobName);

    boolean updateJob(String jobName, String cronExpression);

    boolean updateJob(String jobName, int delayInSeconds, int intervalInSeconds);

    @NonNull
    boolean addJob(@NonNull Task task);

    @NonNull
    boolean pauseJob(@NonNull JobKey jobKey);

    @NonNull
    boolean pauseJob(@NonNull String jobName, @NonNull String jobGroupName);

    @NonNull
    boolean resumeJob(@NonNull JobKey jobKey);

    @NonNull
    boolean resumeJob(@NonNull String jobName, @NonNull String jobGroupName);

    @NonNull
    boolean deleteJob(@NonNull JobKey jobKey);

    @NonNull
    boolean deleteJob(@NonNull String jobName, @NonNull String jobGroupName);

    @NonNull
    boolean modifyJobCron(@NonNull Task task);
}

