package com.github.iappapp.panda.common.job.biz.model;

import java.io.Serializable;
import java.util.Date;

public class JobInfoParam implements Serializable {
    private int id;

    private String appname;

    private int jobGroup;

    private String jobDesc;

    private String author;

    private String alarmEmail;

    private String scheduleType;

    private String scheduleConf;

    private String misfireStrategy;

    private String executorRouteStrategy;

    private String executorHandler;

    private String executorParam;

    private String executorBlockStrategy;

    private int executorTimeout;

    private int executorFailRetryCount;

    private String glueType;

    private String glueSource;

    private String glueRemark;

    private Date glueUpdatetime;

    private String childJobId;

    private int triggerStatus;

    private long triggerLastTime;

    private long triggerNextTime;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getJobGroup() {
        return this.jobGroup;
    }

    public void setJobGroup(int jobGroup) {
        this.jobGroup = jobGroup;
    }

    public String getAppname() {
        return this.appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public String getJobDesc() {
        return this.jobDesc;
    }

    public void setJobDesc(String jobDesc) {
        this.jobDesc = jobDesc;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAlarmEmail() {
        return this.alarmEmail;
    }

    public void setAlarmEmail(String alarmEmail) {
        this.alarmEmail = alarmEmail;
    }

    public String getScheduleType() {
        return this.scheduleType;
    }

    public void setScheduleType(String scheduleType) {
        this.scheduleType = scheduleType;
    }

    public String getScheduleConf() {
        return this.scheduleConf;
    }

    public void setScheduleConf(String scheduleConf) {
        this.scheduleConf = scheduleConf;
    }

    public String getMisfireStrategy() {
        return this.misfireStrategy;
    }

    public void setMisfireStrategy(String misfireStrategy) {
        this.misfireStrategy = misfireStrategy;
    }

    public String getExecutorRouteStrategy() {
        return this.executorRouteStrategy;
    }

    public void setExecutorRouteStrategy(String executorRouteStrategy) {
        this.executorRouteStrategy = executorRouteStrategy;
    }

    public String getExecutorHandler() {
        return this.executorHandler;
    }

    public void setExecutorHandler(String executorHandler) {
        this.executorHandler = executorHandler;
    }

    public String getExecutorParam() {
        return this.executorParam;
    }

    public void setExecutorParam(String executorParam) {
        this.executorParam = executorParam;
    }

    public String getExecutorBlockStrategy() {
        return this.executorBlockStrategy;
    }

    public void setExecutorBlockStrategy(String executorBlockStrategy) {
        this.executorBlockStrategy = executorBlockStrategy;
    }

    public int getExecutorTimeout() {
        return this.executorTimeout;
    }

    public void setExecutorTimeout(int executorTimeout) {
        this.executorTimeout = executorTimeout;
    }

    public int getExecutorFailRetryCount() {
        return this.executorFailRetryCount;
    }

    public void setExecutorFailRetryCount(int executorFailRetryCount) {
        this.executorFailRetryCount = executorFailRetryCount;
    }

    public String getGlueType() {
        return this.glueType;
    }

    public void setGlueType(String glueType) {
        this.glueType = glueType;
    }

    public String getGlueSource() {
        return this.glueSource;
    }

    public void setGlueSource(String glueSource) {
        this.glueSource = glueSource;
    }

    public String getGlueRemark() {
        return this.glueRemark;
    }

    public void setGlueRemark(String glueRemark) {
        this.glueRemark = glueRemark;
    }

    public Date getGlueUpdatetime() {
        return this.glueUpdatetime;
    }

    public void setGlueUpdatetime(Date glueUpdatetime) {
        this.glueUpdatetime = glueUpdatetime;
    }

    public String getChildJobId() {
        return this.childJobId;
    }

    public void setChildJobId(String childJobId) {
        this.childJobId = childJobId;
    }

    public int getTriggerStatus() {
        return this.triggerStatus;
    }

    public void setTriggerStatus(int triggerStatus) {
        this.triggerStatus = triggerStatus;
    }

    public long getTriggerLastTime() {
        return this.triggerLastTime;
    }

    public void setTriggerLastTime(long triggerLastTime) {
        this.triggerLastTime = triggerLastTime;
    }

    public long getTriggerNextTime() {
        return this.triggerNextTime;
    }

    public void setTriggerNextTime(long triggerNextTime) {
        this.triggerNextTime = triggerNextTime;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("JobInfoParam{");
        sb.append("appname='").append(this.appname).append('\'');
        sb.append(", jobDesc='").append(this.jobDesc).append('\'');
        sb.append(", author='").append(this.author).append('\'');
        sb.append(", alarmEmail='").append(this.alarmEmail).append('\'');
        sb.append(", scheduleType='").append(this.scheduleType).append('\'');
        sb.append(", scheduleConf='").append(this.scheduleConf).append('\'');
        sb.append(", misfireStrategy='").append(this.misfireStrategy).append('\'');
        sb.append(", executorRouteStrategy='").append(this.executorRouteStrategy).append('\'');
        sb.append(", executorHandler='").append(this.executorHandler).append('\'');
        sb.append(", executorParam='").append(this.executorParam).append('\'');
        sb.append(", executorBlockStrategy='").append(this.executorBlockStrategy).append('\'');
        sb.append(", executorTimeout=").append(this.executorTimeout);
        sb.append(", executorFailRetryCount=").append(this.executorFailRetryCount);
        sb.append(", glueType='").append(this.glueType).append('\'');
        sb.append(", glueSource='").append(this.glueSource).append('\'');
        sb.append(", glueRemark='").append(this.glueRemark).append('\'');
        sb.append(", glueUpdatetime=").append(this.glueUpdatetime);
        sb.append(", childJobId='").append(this.childJobId).append('\'');
        sb.append(", triggerStatus=").append(this.triggerStatus);
        sb.append(", triggerLastTime=").append(this.triggerLastTime);
        sb.append(", triggerNextTime=").append(this.triggerNextTime);
        sb.append('}');
        return sb.toString();
    }
}
