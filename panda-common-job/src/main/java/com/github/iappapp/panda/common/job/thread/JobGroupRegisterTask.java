package com.github.iappapp.panda.common.job.thread;

import com.github.iappapp.panda.common.job.biz.AdminBiz;
import com.github.iappapp.panda.common.job.biz.model.JobGroupParam;
import com.github.iappapp.panda.common.job.biz.model.ReturnT;
import com.github.iappapp.panda.common.job.enums.AddressTypeEnum;
import com.github.iappapp.panda.common.job.executor.PandaJobExecutor;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobGroupRegisterTask implements Callable<Boolean> {
    private static final Logger logger = LoggerFactory.getLogger(JobGroupRegisterTask.class);

    private String appname;

    private String userName;

    private String password;

    public JobGroupRegisterTask() {
    }

    public JobGroupRegisterTask(String appname, String userName, String password) {
        this.appname = appname;
        this.userName = userName;
        this.password = password;
    }

    public Boolean call() throws Exception {
        JobGroupParam param = new JobGroupParam();
        param.setAppname(this.appname);
        param.setTitle(this.appname);
        param.setAddressType(AddressTypeEnum.AUTO_REGISTER.getCode());
        for (AdminBiz adminBiz : PandaJobExecutor.getAdminBizList()) {
            try {
                ReturnT<String> result = adminBiz.registerJobGroup(param);
                if (result != null && 200 == result.getCode()) {
                    logger.info("panda-job register group success, param:{}, result:{}", param, result);
                    return Boolean.TRUE;
                }
                logger.info("panda-job register group fail, param:{}, result:{}", param, result);
            } catch (Exception e) {
                logger.info("panda-job register group error, param:{}", param, e);
            }
        }
        return Boolean.FALSE;
    }
}
