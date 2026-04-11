package com.github.iappapp.panda.common.job.thread;

import com.github.iappapp.panda.common.job.biz.model.JobInfoParam;
import com.github.iappapp.panda.common.job.executor.PandaJobExecutor;

import java.util.List;
import java.util.concurrent.FutureTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecutorRegistryJobThread {
    private static Logger logger = LoggerFactory.getLogger(ExecutorRegistryJobThread.class);

    private static ExecutorRegistryJobThread instance = new ExecutorRegistryJobThread();

    private Thread registryJobThread;

    public static ExecutorRegistryJobThread getInstance() {
        return instance;
    }

    public void start(final String appname, final String userName, final String password, final List<JobInfoParam> pandaJobList) {
        if (appname == null || appname.trim().length() == 0) {
            logger.warn("panda-job, executor registry config fail, appname is null.");
            return;
        }
        if (PandaJobExecutor.getAdminBizList() == null) {
            logger.warn("panda-job, executor registry config fail, adminAddresses is null.");
            return;
        }
        this.registryJobThread = new Thread(new Runnable() {
            public void run() {
                JobInfoRegisterTask jobInfoRegisterTask = new JobInfoRegisterTask(userName, password, appname);
                for (JobInfoParam param : pandaJobList)
                    jobInfoRegisterTask.addJobInfoTask(param);
                FutureTask<Boolean> futureTask = new FutureTask<>(jobInfoRegisterTask);
                futureTask.run();
                try {
                    boolean result = ((Boolean) futureTask.get()).booleanValue();
                    ExecutorRegistryJobThread.logger.info("panda-job, executor registry jobInfo finish result={}", Boolean.valueOf(result));
                } catch (InterruptedException | java.util.concurrent.ExecutionException ex) {
                    ExecutorRegistryJobThread.logger.info("panda-job, executor registry jobInfo fail ex={}", ex.getMessage());
                }
            }
        });
        this.registryJobThread.setDaemon(true);
        this.registryJobThread.setName("Panda-Job-Registry-Job-Thread");
        this.registryJobThread.start();
    }
}
