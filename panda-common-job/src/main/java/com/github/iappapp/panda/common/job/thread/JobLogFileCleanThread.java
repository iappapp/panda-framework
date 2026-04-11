package com.github.iappapp.panda.common.job.thread;

import com.github.iappapp.panda.common.job.log.PandaJobFileAppender;
import com.github.iappapp.panda.common.job.util.FileUtil;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobLogFileCleanThread {
    private static Logger logger = LoggerFactory.getLogger(JobLogFileCleanThread.class);

    private static JobLogFileCleanThread instance = new JobLogFileCleanThread();

    private Thread localThread;

    public static JobLogFileCleanThread getInstance() {
        return instance;
    }

    private volatile boolean toStop = false;

    public void start(final long logRetentionDays) {
        if (logRetentionDays < 3L)
            return;
        this.localThread = new Thread(new Runnable() {
            public void run() {
                while (!JobLogFileCleanThread.this.toStop) {
                    try {
                        File[] childDirs = (new File(PandaJobFileAppender.getLogPath())).listFiles();
                        if (childDirs != null && childDirs.length > 0) {
                            Calendar todayCal = Calendar.getInstance();
                            todayCal.set(11, 0);
                            todayCal.set(12, 0);
                            todayCal.set(13, 0);
                            todayCal.set(14, 0);
                            Date todayDate = todayCal.getTime();
                            for (File childFile : childDirs) {
                                if (childFile.isDirectory())
                                    if (childFile.getName().indexOf("-") != -1) {
                                        Date logFileCreateDate = null;
                                        try {
                                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                            logFileCreateDate = simpleDateFormat.parse(childFile.getName());
                                        } catch (ParseException e) {
                                            JobLogFileCleanThread.logger.error(e.getMessage(), e);
                                        }
                                        if (logFileCreateDate != null)
                                            if (todayDate.getTime() - logFileCreateDate.getTime() >= logRetentionDays * 86400000L)
                                                FileUtil.deleteRecursively(childFile);
                                    }
                            }
                        }
                    } catch (Exception e) {
                        if (!JobLogFileCleanThread.this.toStop)
                            JobLogFileCleanThread.logger.error(e.getMessage(), e);
                    }
                    try {
                        TimeUnit.DAYS.sleep(1L);
                    } catch (InterruptedException e) {
                        if (!JobLogFileCleanThread.this.toStop)
                            JobLogFileCleanThread.logger.error(e.getMessage(), e);
                    }
                }
                JobLogFileCleanThread.logger.info("panda-job, executor JobLogFileCleanThread thread destory.");
            }
        });
        this.localThread.setDaemon(true);
        this.localThread.setName("Panda-Job-Clean-File-Thread");
        this.localThread.start();
    }

    public void toStop() {
        this.toStop = true;
        if (this.localThread == null)
            return;
        this.localThread.interrupt();
        try {
            this.localThread.join();
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
