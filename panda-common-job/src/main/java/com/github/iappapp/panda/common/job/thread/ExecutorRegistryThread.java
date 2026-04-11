package com.github.iappapp.panda.common.job.thread;

import com.github.iappapp.panda.common.job.biz.AdminBiz;
import com.github.iappapp.panda.common.job.biz.model.RegistryParam;
import com.github.iappapp.panda.common.job.biz.model.ReturnT;
import com.github.iappapp.panda.common.job.enums.RegistryConfig;
import com.github.iappapp.panda.common.job.executor.PandaJobExecutor;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecutorRegistryThread {
    private static Logger logger = LoggerFactory.getLogger(ExecutorRegistryThread.class);

    private static ExecutorRegistryThread instance = new ExecutorRegistryThread();

    private Thread registryThread;

    public static ExecutorRegistryThread getInstance() {
        return instance;
    }

    private volatile boolean toStop = false;

    public void start(final String appname, final String address) {
        if (appname == null || appname.trim().length() == 0) {
            logger.warn("panda-job, executor registry config fail, appname is null.");
            return;
        }
        if (PandaJobExecutor.getAdminBizList() == null) {
            logger.warn("panda-job, executor registry config fail, adminAddresses is null.");
            return;
        }
        this.registryThread = new Thread(new Runnable() {
            public void run() {
                while (!ExecutorRegistryThread.this.toStop) {
                    try {
                        RegistryParam registryParam = new RegistryParam(RegistryConfig.RegistryType.EXECUTOR.name(), appname, address);
                        for (AdminBiz adminBiz : PandaJobExecutor.getAdminBizList()) {
                            try {
                                ReturnT<String> registryResult = adminBiz.registry(registryParam);
                                if (registryResult != null && 200 == registryResult.getCode()) {
                                    registryResult = ReturnT.SUCCESS;
                                    ExecutorRegistryThread.logger.debug("panda-job registry success, registryParam:{}, registryResult:{}", new Object[]{registryParam, registryResult});
                                    break;
                                }
                                ExecutorRegistryThread.logger.info("panda-job registry fail, registryParam:{}, registryResult:{}", new Object[]{registryParam, registryResult});
                            } catch (Exception e) {
                                ExecutorRegistryThread.logger.info("panda-job registry error, registryParam:{}", registryParam, e);
                            }
                        }
                    } catch (Exception e) {
                        if (!ExecutorRegistryThread.this.toStop)
                            ExecutorRegistryThread.logger.error(e.getMessage(), e);
                    }
                    try {
                        if (!ExecutorRegistryThread.this.toStop)
                            TimeUnit.SECONDS.sleep(30L);
                    } catch (InterruptedException e) {
                        if (!ExecutorRegistryThread.this.toStop)
                            ExecutorRegistryThread.logger.warn("panda-job, executor registry thread interrupted, error msg:{}", e.getMessage());
                    }
                }
                try {
                    RegistryParam registryParam = new RegistryParam(RegistryConfig.RegistryType.EXECUTOR.name(), appname, address);
                    for (AdminBiz adminBiz : PandaJobExecutor.getAdminBizList()) {
                        try {
                            ReturnT<String> registryResult = adminBiz.registryRemove(registryParam);
                            if (registryResult != null && 200 == registryResult.getCode()) {
                                registryResult = ReturnT.SUCCESS;
                                ExecutorRegistryThread.logger.info("panda-job registry-remove success, registryParam:{}, registryResult:{}", new Object[]{registryParam, registryResult});
                                break;
                            }
                            ExecutorRegistryThread.logger.info("panda-job registry-remove fail, registryParam:{}, registryResult:{}", new Object[]{registryParam, registryResult});
                        } catch (Exception e) {
                            if (!ExecutorRegistryThread.this.toStop)
                                ExecutorRegistryThread.logger.info("panda-job registry-remove error, registryParam:{}", registryParam, e);
                        }
                    }
                } catch (Exception e) {
                    if (!ExecutorRegistryThread.this.toStop)
                        ExecutorRegistryThread.logger.error(e.getMessage(), e);
                }
                ExecutorRegistryThread.logger.info("panda-job, executor registry thread destory.");
            }
        });
        this.registryThread.setDaemon(true);
        this.registryThread.setName("Panda-Job-Registry-Thread");
        this.registryThread.start();
    }

    public void toStop() {
        this.toStop = true;
        if (this.registryThread != null) {
            this.registryThread.interrupt();
            try {
                this.registryThread.join();
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public void toStart() {
        this.toStop = false;
    }
}
