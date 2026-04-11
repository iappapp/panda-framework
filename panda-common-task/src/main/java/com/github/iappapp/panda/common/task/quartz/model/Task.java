/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.NonNull
 *  org.quartz.Job
 *  org.quartz.JobKey
 */
package com.github.iappapp.panda.common.task.quartz.model;

import java.util.Map;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import org.quartz.Job;
import org.quartz.JobKey;

@ToString
@EqualsAndHashCode
public class Task {
    @NonNull
    private String name;
    @NonNull
    private String group;
    @NonNull
    private String cron;
    private String desc;
    private String jobClass;
    private Class<? extends Job> jobClazz;
    private Map<?, ?> jobDataMap;

    public JobKey getJobKey() {
        return JobKey.jobKey(this.name, this.group);
    }

    Task(@NonNull String name, @NonNull String group, @NonNull String cron,
         String desc, String jobClass, Class<? extends Job> jobClazz, Map<?, ?> jobDataMap) {
        if (name == null) {
            throw new NullPointerException("name is marked non-null but is null");
        }
        if (group == null) {
            throw new NullPointerException("group is marked non-null but is null");
        }
        if (cron == null) {
            throw new NullPointerException("cron is marked non-null but is null");
        }
        this.name = name;
        this.group = group;
        this.cron = cron;
        this.desc = desc;
        this.jobClass = jobClass;
        this.jobClazz = jobClazz;
        this.jobDataMap = jobDataMap;
    }

    public static TaskBuilder builder() {
        return new TaskBuilder();
    }

    @NonNull
    public String getName() {
        return this.name;
    }

    @NonNull
    public String getGroup() {
        return this.group;
    }

    @NonNull
    public String getCron() {
        return this.cron;
    }

    public String getDesc() {
        return this.desc;
    }

    public String getJobClass() {
        return this.jobClass;
    }

    public Class<? extends Job> getJobClazz() {
        return this.jobClazz;
    }

    public Map<?, ?> getJobDataMap() {
        return this.jobDataMap;
    }

    public void setName(@NonNull String name) {
        if (name == null) {
            throw new NullPointerException("name is marked non-null but is null");
        }
        this.name = name;
    }

    public void setGroup(@NonNull String group) {
        if (group == null) {
            throw new NullPointerException("group is marked non-null but is null");
        }
        this.group = group;
    }

    public void setCron(@NonNull String cron) {
        if (cron == null) {
            throw new NullPointerException("cron is marked non-null but is null");
        }
        this.cron = cron;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setJobClass(String jobClass) {
        this.jobClass = jobClass;
    }

    public void setJobClazz(Class<? extends Job> jobClazz) {
        this.jobClazz = jobClazz;
    }

    public void setJobDataMap(Map<?, ?> jobDataMap) {
        this.jobDataMap = jobDataMap;
    }

    public static class TaskBuilder {
        private String name;
        private String group;
        private String cron;
        private String desc;
        private String jobClass;
        private Class<? extends Job> jobClazz;
        private Map<?, ?> jobDataMap;

        TaskBuilder() {
        }

        public TaskBuilder name(@NonNull String name) {
            if (name == null) {
                throw new NullPointerException("name is marked non-null but is null");
            }
            this.name = name;
            return this;
        }

        public TaskBuilder group(@NonNull String group) {
            if (group == null) {
                throw new NullPointerException("group is marked non-null but is null");
            }
            this.group = group;
            return this;
        }

        public TaskBuilder cron(@NonNull String cron) {
            if (cron == null) {
                throw new NullPointerException("cron is marked non-null but is null");
            }
            this.cron = cron;
            return this;
        }

        public TaskBuilder desc(String desc) {
            this.desc = desc;
            return this;
        }

        public TaskBuilder jobClass(String jobClass) {
            this.jobClass = jobClass;
            return this;
        }

        public TaskBuilder jobClazz(Class<? extends Job> jobClazz) {
            this.jobClazz = jobClazz;
            return this;
        }

        public TaskBuilder jobDataMap(Map<?, ?> jobDataMap) {
            this.jobDataMap = jobDataMap;
            return this;
        }

        public Task build() {
            return new Task(this.name, this.group, this.cron, this.desc, this.jobClass, this.jobClazz, this.jobDataMap);
        }

        public String toString() {
            return "Task.TaskBuilder(name=" + this.name + ", group=" + this.group + ", cron=" + this.cron + ", desc=" + this.desc + ", jobClass=" + this.jobClass + ", jobClazz=" + this.jobClazz + ", jobDataMap=" + this.jobDataMap + ")";
        }
    }
}

