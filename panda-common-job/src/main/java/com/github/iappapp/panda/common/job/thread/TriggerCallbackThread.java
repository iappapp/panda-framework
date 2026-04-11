package com.github.iappapp.panda.common.job.thread;

import com.github.iappapp.panda.common.job.biz.AdminBiz;
import com.github.iappapp.panda.common.job.biz.model.HandleCallbackParam;
import com.github.iappapp.panda.common.job.biz.model.ReturnT;
import com.github.iappapp.panda.common.job.context.PandaJobContext;
import com.github.iappapp.panda.common.job.context.PandaJobHelper;
import com.github.iappapp.panda.common.job.executor.PandaJobExecutor;
import com.github.iappapp.panda.common.job.log.PandaJobFileAppender;
import com.github.iappapp.panda.common.job.util.FileUtil;
import com.github.iappapp.panda.common.job.util.JdkSerializeTool;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TriggerCallbackThread {
    private static Logger logger = LoggerFactory.getLogger(TriggerCallbackThread.class);

    private static TriggerCallbackThread instance = new TriggerCallbackThread();

    public static TriggerCallbackThread getInstance() {
        return instance;
    }

    private LinkedBlockingQueue<HandleCallbackParam> callBackQueue = new LinkedBlockingQueue<>();

    private Thread triggerCallbackThread;

    private Thread triggerRetryCallbackThread;

    public static void pushCallBack(HandleCallbackParam callback) {
        (getInstance()).callBackQueue.add(callback);
        logger.debug("panda-job, push callback request, logId:{}", Long.valueOf(callback.getLogId()));
    }

    private volatile boolean toStop = false;

    public void start() {
        if (PandaJobExecutor.getAdminBizList() == null) {
            logger.warn("panda-job, executor callback config fail, adminAddresses is null.");
            return;
        }
        this.triggerCallbackThread = new Thread(new Runnable() {
            public void run() {
                while (!TriggerCallbackThread.this.toStop) {
                    try {
                        HandleCallbackParam callback = callBackQueue.take();
                        if (callback != null) {
                            List<HandleCallbackParam> callbackParamList = new ArrayList<>();
                            int drainToNum = callBackQueue.drainTo(callbackParamList);
                            callbackParamList.add(callback);
                            if (callbackParamList != null && callbackParamList.size() > 0)
                                TriggerCallbackThread.this.doCallback(callbackParamList);
                        }
                    } catch (Exception e) {
                        if (!TriggerCallbackThread.this.toStop)
                            TriggerCallbackThread.logger.error(e.getMessage(), e);
                    }
                }
                try {
                    List<HandleCallbackParam> callbackParamList = new ArrayList<>();
                    int drainToNum = callBackQueue.drainTo(callbackParamList);
                    if (callbackParamList != null && callbackParamList.size() > 0)
                        TriggerCallbackThread.this.doCallback(callbackParamList);
                } catch (Exception e) {
                    if (!TriggerCallbackThread.this.toStop)
                        TriggerCallbackThread.logger.error(e.getMessage(), e);
                }
                TriggerCallbackThread.logger.info("panda-job, executor callback thread destory.");
            }
        });
        this.triggerCallbackThread.setDaemon(true);
        this.triggerCallbackThread.setName("Panda-Job-Trigger-Callback-Thread");
        this.triggerCallbackThread.start();
        this.triggerRetryCallbackThread = new Thread(new Runnable() {
            public void run() {
                while (!TriggerCallbackThread.this.toStop) {
                    try {
                        TriggerCallbackThread.this.retryFailCallbackFile();
                    } catch (Exception e) {
                        if (!TriggerCallbackThread.this.toStop)
                            TriggerCallbackThread.logger.error(e.getMessage(), e);
                    }
                    try {
                        TimeUnit.SECONDS.sleep(30L);
                    } catch (InterruptedException e) {
                        if (!TriggerCallbackThread.this.toStop)
                            TriggerCallbackThread.logger.error(e.getMessage(), e);
                    }
                }
                TriggerCallbackThread.logger.info("panda-job, executor retry callback thread destory.");
            }
        });
        this.triggerRetryCallbackThread.setDaemon(true);
        this.triggerRetryCallbackThread.start();
    }

    public void toStop() {
        this.toStop = true;
        if (this.triggerCallbackThread != null) {
            this.triggerCallbackThread.interrupt();
            try {
                this.triggerCallbackThread.join();
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }
        if (this.triggerRetryCallbackThread != null) {
            this.triggerRetryCallbackThread.interrupt();
            try {
                this.triggerRetryCallbackThread.join();
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    private void doCallback(List<HandleCallbackParam> callbackParamList) {
        boolean callbackRet = false;
        for (AdminBiz adminBiz : PandaJobExecutor.getAdminBizList()) {
            try {
                ReturnT<String> callbackResult = adminBiz.callback(callbackParamList);
                if (callbackResult != null && 200 == callbackResult.getCode()) {
                    callbackLog(callbackParamList, "<br>panda-job job callback finish.");
                    callbackRet = true;
                    break;
                }
                callbackLog(callbackParamList, "<br>panda-job job callback fail, callbackResult:" + callbackResult);
            } catch (Exception e) {
                callbackLog(callbackParamList, "<br>panda-job job callback error, errorMsg:" + e.getMessage());
            }
        }
        if (!callbackRet)
            appendFailCallbackFile(callbackParamList);
    }

    private void callbackLog(List<HandleCallbackParam> callbackParamList, String logContent) {
        for (HandleCallbackParam callbackParam : callbackParamList) {
            String logFileName = PandaJobFileAppender.makeLogFileName(new Date(callbackParam.getLogDateTim()), callbackParam.getLogId());
            PandaJobContext.setPandaJobContext(new PandaJobContext(-1L, null, logFileName, -1, -1));
            PandaJobHelper.log(logContent, new Object[0]);
        }
    }

    private static String failCallbackFilePath = PandaJobFileAppender.getLogPath().concat(File.separator).concat("callbacklog").concat(File.separator);

    private static String failCallbackFileName = failCallbackFilePath.concat("panda-job-callback-{x}").concat(".log");

    private void appendFailCallbackFile(List<HandleCallbackParam> callbackParamList) {
        if (callbackParamList == null || callbackParamList.size() == 0)
            return;
        byte[] callbackParamList_bytes = JdkSerializeTool.serialize(callbackParamList);
        File callbackLogFile = new File(failCallbackFileName.replace("{x}", String.valueOf(System.currentTimeMillis())));
        if (callbackLogFile.exists())
            for (int i = 0; i < 100; i++) {
                callbackLogFile = new File(failCallbackFileName.replace("{x}", String.valueOf(System.currentTimeMillis()).concat("-").concat(String.valueOf(i))));
                if (!callbackLogFile.exists())
                    break;
            }
        FileUtil.writeFileContent(callbackLogFile, callbackParamList_bytes);
    }

    private void retryFailCallbackFile() {
        File callbackLogPath = new File(failCallbackFilePath);
        if (!callbackLogPath.exists())
            return;
        if (callbackLogPath.isFile())
            callbackLogPath.delete();
        if (!callbackLogPath.isDirectory() || callbackLogPath.list() == null || (callbackLogPath.list()).length <= 0)
            return;
        for (File callbaclLogFile : callbackLogPath.listFiles()) {
            byte[] callbackParamList_bytes = FileUtil.readFileContent(callbaclLogFile);
            if (callbackParamList_bytes == null || callbackParamList_bytes.length < 1) {
                callbaclLogFile.delete();
            } else {
                List<HandleCallbackParam> callbackParamList = (List<HandleCallbackParam>) JdkSerializeTool.deserialize(callbackParamList_bytes, List.class);
                callbaclLogFile.delete();
                doCallback(callbackParamList);
            }
        }
    }
}
