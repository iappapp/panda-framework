package com.github.iappapp.panda.common.job.thread;

import com.github.iappapp.panda.common.job.executor.PandaJobExecutor;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecutorRegistryGroupThread {
    private static Logger logger = LoggerFactory.getLogger(ExecutorRegistryGroupThread.class);

    private static ExecutorRegistryGroupThread instance = new ExecutorRegistryGroupThread();

    private Thread registryGroupThread;

    public static ExecutorRegistryGroupThread getInstance() {
        return instance;
    }

    public void start(final String appname, final String userName, final String password) {
        if (appname == null || appname.trim().length() == 0) {
            logger.warn("panda-job, executor registry config fail, appname is null.");
            return;
        }
        if (PandaJobExecutor.getAdminBizList() == null) {
            logger.warn("panda-job, executor registry config fail, adminAddresses is null.");
            return;
        }
        this.registryGroupThread = new Thread(new Runnable() {
            public void run() {
                boolean jobGroupRegister = false;
                while (!jobGroupRegister) {
                    FutureTask<Boolean> futureTask = new FutureTask<>(new JobGroupRegisterTask(appname, userName, password));
                    futureTask.run();
                    try {
                        jobGroupRegister = ((Boolean) futureTask.get()).booleanValue();
                        ExecutorRegistryGroupThread.logger.info("panda-job, executor registry jobGroup fail result={}", Boolean.valueOf(jobGroupRegister));
                        Thread.sleep(30000L);
                    } catch (ExecutionException | InterruptedException ex) {
                        ExecutorRegistryGroupThread.logger.info("panda-job, executor registry jobGroup fail ex={}", ex.getMessage());
                    }
                }
            }
        });
        this.registryGroupThread.setDaemon(true);
        this.registryGroupThread.setName("Panda-Job-Registry-Group-Thread");
        this.registryGroupThread.start();
    }
}
