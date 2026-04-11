package com.github.iappapp.panda.common.job.thread;

import com.github.iappapp.panda.common.job.biz.AdminBiz;
import com.github.iappapp.panda.common.job.biz.model.JobGroupParam;
import com.github.iappapp.panda.common.job.biz.model.JobInfoParam;
import com.github.iappapp.panda.common.job.biz.model.ReturnT;
import com.github.iappapp.panda.common.job.enums.TriggerStatusEnum;
import com.github.iappapp.panda.common.job.executor.PandaJobExecutor;

import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingDeque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobInfoRegisterTask implements Callable<Boolean> {
    private static final Logger logger = LoggerFactory.getLogger(JobInfoRegisterTask.class);

    private String userName;

    private String password;

    private String appname;

    private LinkedBlockingDeque<JobInfoParam> jobInfoParamLinkedBlockingDeque = new LinkedBlockingDeque<>(256);

    public JobInfoRegisterTask(String userName, String password, String appname) {
        this.userName = userName;
        this.password = password;
        this.appname = appname;
    }

    public void addJobInfoTask(JobInfoParam jobInfoParam) {
        this.jobInfoParamLinkedBlockingDeque.addLast(jobInfoParam);
    }

    public Boolean call() throws Exception {
        while (!this.jobInfoParamLinkedBlockingDeque.isEmpty()) {
            JobInfoParam param = this.jobInfoParamLinkedBlockingDeque.pollFirst();
            boolean jobInfoResult = false;
            for (AdminBiz adminBiz : PandaJobExecutor.getAdminBizList()) {
                try {

                    ReturnT<JobGroupParam> jobGroup = adminBiz.findJobGroup(this.appname);
                    if (null == jobGroup || jobGroup.getContent() == null) {
                        logger.info("panda-job registry job ignore group={} not exists", this.appname);
                        continue;
                    }
                    ReturnT<JobInfoParam> jobInfo = adminBiz.findJobInfo(this.appname, jobGroup.getContent().getId(), param.getExecutorHandler(), null);
                    param.setJobGroup(( jobGroup.getContent()).getId());
                    if (jobInfo != null && jobInfo.getContent() != null) {
                        logger.info("panda-job registry job ignore jobInfo={} exists", jobInfo.getContent());
                        param.setId(( jobInfo.getContent()).getId());
                        ReturnT<String> updateResult = adminBiz.updateJobInfo(param);
                        if (updateResult != null && updateResult.getCode() == 200) {
                            logger.info("panda-job registry job update jobInfo={} exists updateResult={}", jobInfo.getContent(), updateResult);
                            jobInfoResult = true;
                            if (TriggerStatusEnum.STATUS_CLOSE.getCode() == ( jobInfo.getContent()).getTriggerStatus() && param
                                    .getTriggerStatus() == TriggerStatusEnum.STATUS_OPEN.getCode())
                                adminBiz.startJob(( jobInfo.getContent()).getId());
                            if (TriggerStatusEnum.STATUS_OPEN.getCode() == ( jobInfo.getContent()).getTriggerStatus() && param
                                    .getTriggerStatus() == TriggerStatusEnum.STATUS_CLOSE.getCode())
                                adminBiz.stopJob(( jobInfo.getContent()).getId());
                            break;
                        }
                    }
                    ReturnT<String> registryResult = adminBiz.registerJobInfo(param);
                    if (registryResult != null && 200 == registryResult.getCode()) {
                        logger.info("panda-job registry job success, param:{}, registryResult:{}", new Object[]{param, registryResult});
                        jobInfoResult = true;
                        break;
                    }
                    logger.info("panda-job registry job fail, param:{}, registryResult:{}", new Object[]{param, registryResult});
                } catch (Exception e) {
                    logger.info("panda-job registry job error, param:{}", param, e);
                }
            }
            try {
                if (!jobInfoResult)
                    Thread.sleep(30000L);
            } catch (InterruptedException ex) {
                logger.info("panda-job registry job sleep error", ex);
            }
            if (!jobInfoResult)
                this.jobInfoParamLinkedBlockingDeque.addLast(param);
        }
        return Boolean.valueOf(true);
    }

    public JobInfoRegisterTask() {
    }
}
