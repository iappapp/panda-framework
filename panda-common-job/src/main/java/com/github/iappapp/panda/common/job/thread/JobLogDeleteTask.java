package com.github.iappapp.panda.common.job.thread;

import com.github.iappapp.panda.common.job.biz.AdminBiz;
import com.github.iappapp.panda.common.job.biz.model.JobGroupParam;
import com.github.iappapp.panda.common.job.biz.model.JobInfoParam;
import com.github.iappapp.panda.common.job.biz.model.ReturnT;
import com.github.iappapp.panda.common.job.executor.PandaJobExecutor;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobLogDeleteTask implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(JobLogDeleteTask.class);

    private String userName;

    private String password;

    private String appname;

    public JobLogDeleteTask() {
    }

    public JobLogDeleteTask(String userName, String password, String appname) {
        this.userName = userName;
        this.password = password;
        this.appname = appname;
    }

    public void run() {
        List<JobInfoParam> jobInfoParamList = new ArrayList<>();
        for (AdminBiz adminBiz : PandaJobExecutor.getAdminBizList()) {
            try {
                ReturnT<String> returnT = adminBiz.login(this.userName, this.password);
                if (returnT.getCode() != 200) {
                    logger.info("panda-job clean log job fail group={} login fail", this.appname);
                    continue;
                }
                ReturnT<JobGroupParam> jobGroup = adminBiz.findJobGroup(this.appname);
                if (null == jobGroup || jobGroup.getContent() == null) {
                    logger.info("panda-job clean log job ignore group={} not exists", this.appname);
                    continue;
                }
                ReturnT<List<JobInfoParam>> jobInfoList = adminBiz.findJobInfo(this.appname, returnT.getContent(), ( jobGroup.getContent()).getId());
                if (jobInfoList != null && jobInfoList.getContent() != null && ((List) jobInfoList.getContent()).size() > 0) {
                    jobInfoParamList = jobInfoList.getContent();
                    break;
                }
            } catch (Exception e) {
                logger.info("panda-job clean log job error", e);
            }
        }
        for (JobInfoParam param : jobInfoParamList) {
            for (AdminBiz adminBiz : PandaJobExecutor.getAdminBizList()) {
                try {
                    ReturnT<String> result = adminBiz.cleanLog(param.getJobGroup(), param.getId(), 5);
                    logger.info("panda-job clean log job jobInfo={} result={}", param, result);
                    if (result != null && 200 == result.getCode()) {
                        break;
                    }
                    Thread.sleep(10000L);
                } catch (Exception ex) {
                    logger.info("panda-job clean log fail", ex);
                }
            }
        }
    }
}
