package com.github.iappapp.panda.common.job.thread;

import com.github.iappapp.panda.common.job.executor.PandaJobExecutor;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecutorCleanLogThread {
    private static Logger logger = LoggerFactory.getLogger(ExecutorCleanLogThread.class);

    private static ExecutorCleanLogThread instance = new ExecutorCleanLogThread();

    private Thread cleanLogThread;

    public static ExecutorCleanLogThread getInstance() {
        return instance;
    }

    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
        private final AtomicInteger threadNumber = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            SecurityManager s = System.getSecurityManager();
            ThreadGroup group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            String namePrefix = "panda-job-Scheduled-";
            Thread t = new Thread(group, r, namePrefix + this.threadNumber.getAndIncrement(), 0L);
            t.setDaemon(true);
            t.setPriority(5);
            return t;
        }
    });

    public void start(final String appname, final String userName, final String password) {
        if (appname == null || appname.trim().length() == 0) {
            logger.warn("panda-job, executor clean log config fail, appname is null.");
            return;
        }
        if (PandaJobExecutor.getAdminBizList() == null) {
            logger.warn("panda-job, executor clean log config fail, adminAddresses is null.");
            return;
        }
        this.cleanLogThread = new Thread(new Runnable() {
            public void run() {
                ExecutorCleanLogThread.this.scheduledExecutorService
                        .scheduleAtFixedRate(new JobLogDeleteTask(userName, password, appname), 0L, 2L, TimeUnit.HOURS);
            }
        });
        this.cleanLogThread.setDaemon(true);
        this.cleanLogThread.setName("Panda-Job-Clean-Log-Thread");
        this.cleanLogThread.start();
    }

    public void toStop() {
        this.scheduledExecutorService.shutdown();
    }
}
