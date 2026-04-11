package com.github.iappapp.panda.common.job.biz.client;

import com.github.iappapp.panda.common.job.biz.AdminBiz;
import com.github.iappapp.panda.common.job.biz.model.JobGroupParam;
import com.github.iappapp.panda.common.job.biz.model.JobInfoParam;
import com.github.iappapp.panda.common.job.biz.model.ReturnT;
import com.github.iappapp.panda.common.job.config.PandaJobProperties;
import com.github.iappapp.panda.common.job.enums.TriggerStatusEnum;
import com.github.iappapp.panda.common.job.exception.PandaJobExistException;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;

public class PandaJobClient implements SmartInitializingSingleton {
    private static final Logger log = LoggerFactory.getLogger(PandaJobClient.class);

    @Autowired
    private PandaJobProperties pandaJobProperties;

    private List<AdminBiz> adminBizList;

    @Override
    public void afterSingletonsInstantiated() {
        if (pandaJobProperties.getAdminAddresses() != null
                && !pandaJobProperties.getAdminAddresses().trim().isEmpty()) {
            for (String address : pandaJobProperties.getAdminAddresses().trim().split(",")) {
                if (address != null && !address.trim().isEmpty()) {
                    AdminBiz adminBiz = new AdminBizClient(address.trim(), null,
                            pandaJobProperties.getUserName(), pandaJobProperties.getPassword());
                    if (adminBizList == null) {
                        adminBizList = new ArrayList<>();
                    }
                    adminBizList.add(adminBiz);
                }
            }
        }
    }

    @Deprecated
    public ReturnT<String> stopJob(String appname, String jobHandler) {
        for (AdminBiz adminBiz : adminBizList) {
            ReturnT<String> result = adminBiz.stopJob(appname, jobHandler, null);
            if (result != null && result.getCode() == 200) {
                return result;
            }
        }
        return ReturnT.FAIL;
    }

    public ReturnT<String> stopJob(String appname, String jobHandler, String jobDesc) {
        for (AdminBiz adminBiz : adminBizList) {
            ReturnT<String> result = adminBiz.stopJob(appname, jobHandler, jobDesc);
            if (result != null && result.getCode() == 200) {
                return result;
            }
        }
        return ReturnT.FAIL;
    }

    @Deprecated
    public ReturnT<String> startJob(String appname, String jobHandler) {
        for (AdminBiz adminBiz : adminBizList) {
            ReturnT<String> result = adminBiz.startJob(appname, jobHandler, null);
            if (result != null && result.getCode() == 200) {
                return result;
            }
        }
        return ReturnT.FAIL;
    }

    public ReturnT<String> startJob(String appname, String jobHandler, String jobDesc) {
        for (AdminBiz adminBiz : adminBizList) {
            ReturnT<String> result = adminBiz.startJob(appname, jobHandler, jobDesc);
            if (result != null && result.getCode() == 200) {
                return result;
            }
        }
        return ReturnT.FAIL;
    }

    @Deprecated
    public ReturnT<String> triggerJob(String appname, String jobHandler, String executorParam) {
        for (AdminBiz adminBiz : adminBizList) {
            ReturnT<String> result = adminBiz.triggerJob(appname, jobHandler, executorParam, null);
            if (result != null && result.getCode() == 200) {
                return result;
            }
        }
        return ReturnT.FAIL;
    }

    public ReturnT<String> triggerJob(String appname, String jobHandler, String executorParam, String jobDesc) {
        for (AdminBiz adminBiz : adminBizList) {
            ReturnT<String> result = adminBiz.triggerJob(appname, jobHandler, executorParam, jobDesc);
            if (result != null && result.getCode() == 200) {
                return result;
            }
        }
        return ReturnT.FAIL;
    }

    public ReturnT<String> registerJob(JobInfoParam jobParam) {
        for (AdminBiz adminBiz : adminBizList) {
            ReturnT<JobGroupParam> jobGroup = adminBiz.findJobGroup(jobParam.getAppname());
            if (jobGroup == null || jobGroup.getContent() == null)
                continue;
            jobParam.setJobGroup((jobGroup.getContent()).getId());
            ReturnT<JobInfoParam> jobParamReturnT = adminBiz.findJobInfo(jobParam.getAppname(), jobParam.getJobGroup(), jobParam.getExecutorHandler(), jobParam.getJobDesc());
            if (null != jobParamReturnT && null != jobParamReturnT.getContent()) {
                throw new PandaJobExistException();
            }
            ReturnT<String> result = adminBiz.registerJobInfo(jobParam);
            if (result != null && result.getCode() == 200) {
                if (TriggerStatusEnum.STATUS_OPEN.getCode() == jobParam.getTriggerStatus()) {
                    adminBiz.startJob(Integer.parseInt(result.getContent()));
                }
                return result;
            }
        }
        return ReturnT.FAIL;
    }

    public ReturnT<String> removeJob(String appName, String jobHandler, String jobDesc) {
        for (AdminBiz adminBiz : adminBizList) {
            ReturnT<String> result = adminBiz.removeJob(appName, jobHandler, jobDesc);
            if (result != null && result.getCode() == 200) {
                return result;
            }
        }
        return ReturnT.FAIL;
    }
}
