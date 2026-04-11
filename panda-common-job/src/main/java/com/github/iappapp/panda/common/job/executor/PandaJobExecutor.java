package com.github.iappapp.panda.common.job.executor;

import com.github.iappapp.panda.common.job.biz.AdminBiz;
import com.github.iappapp.panda.common.job.biz.client.AdminBizClient;
import com.github.iappapp.panda.common.job.handler.IJobHandler;
import com.github.iappapp.panda.common.job.log.PandaJobFileAppender;
import com.github.iappapp.panda.common.job.server.EmbedServer;
import com.github.iappapp.panda.common.job.thread.ExecutorCleanLogThread;
import com.github.iappapp.panda.common.job.thread.JobLogFileCleanThread;
import com.github.iappapp.panda.common.job.thread.JobThread;
import com.github.iappapp.panda.common.job.thread.TriggerCallbackThread;
import com.github.iappapp.panda.common.job.util.IpUtil;
import com.github.iappapp.panda.common.job.util.NetUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PandaJobExecutor {
    private static final Logger logger = LoggerFactory.getLogger(PandaJobExecutor.class);

    private String adminAddresses;

    private String accessToken;

    private String appname;

    private String address;

    private String ip;

    private int port;

    private String logPath;

    private int logRetentionDays;

    private String username;

    private String password;

    private static List<AdminBiz> adminBizList;

    public void setAdminAddresses(String adminAddresses) {
        this.adminAddresses = adminAddresses;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    public void setLogRetentionDays(int logRetentionDays) {
        this.logRetentionDays = logRetentionDays;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAdminAddresses() {
        return this.adminAddresses;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public String getAppname() {
        return this.appname;
    }

    public String getAddress() {
        return this.address;
    }

    public String getIp() {
        return this.ip;
    }

    public int getPort() {
        return this.port;
    }

    public String getLogPath() {
        return this.logPath;
    }

    public int getLogRetentionDays() {
        return this.logRetentionDays;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public void start() throws Exception {
        PandaJobFileAppender.initLogPath(this.logPath);
        initAdminBizList(this.adminAddresses, this.accessToken, this.username, this.password);
        JobLogFileCleanThread.getInstance().start(this.logRetentionDays);
        TriggerCallbackThread.getInstance().start();
        initEmbedServer(this.address, this.ip, this.port, this.appname, this.accessToken);
    }

    public void destroy() {
        stopEmbedServer();
        if (jobThreadRepository.size() > 0) {
            for (Map.Entry<Integer, JobThread> item : jobThreadRepository.entrySet()) {
                JobThread oldJobThread = removeJobThread((item.getKey()).intValue(), "web container destroy and kill the job.");
                if (oldJobThread != null) {
                    try {
                        oldJobThread.join();
                    } catch (InterruptedException e) {
                        logger.error("panda-job, JobThread destroy(join) error, jobId:{}", item.getKey(), e);
                    }
                }
            }
            jobThreadRepository.clear();
        }
        jobHandlerRepository.clear();
        JobLogFileCleanThread.getInstance().toStop();
        TriggerCallbackThread.getInstance().toStop();
        ExecutorCleanLogThread.getInstance().toStop();
    }

    private void initAdminBizList(String adminAddresses, String accessToken, String username, String password) throws Exception {
        if (adminAddresses != null && adminAddresses.trim().length() > 0)
            for (String address : adminAddresses.trim().split(",")) {
                if (address != null && address.trim().length() > 0) {
                    AdminBizClient adminBizClient = new AdminBizClient(address.trim(), accessToken, username, password);
                    if (adminBizList == null) {
                        adminBizList = new ArrayList<>();
                    }
                    adminBizList.add(adminBizClient);
                }
            }
    }

    public static List<AdminBiz> getAdminBizList() {
        return adminBizList;
    }

    private EmbedServer embedServer = null;

    private void initEmbedServer(String address, String ip, int port, String appname, String accessToken) throws Exception {
        port = (port > 0) ? port : NetUtil.findAvailablePort(9999);
        ip = (ip != null && ip.trim().length() > 0) ? ip : IpUtil.getIp();
        if (address == null || address.trim().length() == 0) {
            String ip_port_address = IpUtil.getIpPort(ip, port);
            address = "http://{ip_port}/".replace("{ip_port}", ip_port_address);
        }
        if (accessToken == null || accessToken.trim().length() == 0) {
            logger.warn("panda-job accessToken is empty. To ensure system security, please set the accessToken.");
        }
        this.embedServer = new EmbedServer();
        this.embedServer.start(address, port, appname, accessToken);
    }

    private void stopEmbedServer() {
        if (this.embedServer != null) {
            try {
                this.embedServer.stop();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    private static ConcurrentMap<String, IJobHandler> jobHandlerRepository = new ConcurrentHashMap<>();

    public static IJobHandler loadJobHandler(String name) {
        return jobHandlerRepository.get(name);
    }

    public static IJobHandler registJobHandler(String name, IJobHandler jobHandler) {
        logger.info("panda-job register jobhandler success, name:{}, jobHandler:{}", name, jobHandler);
        return jobHandlerRepository.put(name, jobHandler);
    }

    private static ConcurrentMap<Integer, JobThread> jobThreadRepository = new ConcurrentHashMap<>();

    public static JobThread registJobThread(int jobId, IJobHandler handler, String removeOldReason) {
        JobThread newJobThread = new JobThread(jobId, handler);
        newJobThread.start();
        logger.info("panda-job regist JobThread success, jobId:{}, handler:{}", jobId, handler);
        JobThread oldJobThread = jobThreadRepository.put(Integer.valueOf(jobId), newJobThread);
        if (oldJobThread != null) {
            oldJobThread.toStop(removeOldReason);
            oldJobThread.interrupt();
        }
        return newJobThread;
    }

    public static JobThread removeJobThread(int jobId, String removeOldReason) {
        JobThread oldJobThread = jobThreadRepository.remove(Integer.valueOf(jobId));
        if (oldJobThread != null) {
            oldJobThread.toStop(removeOldReason);
            oldJobThread.interrupt();
            return oldJobThread;
        }
        return null;
    }

    public static JobThread loadJobThread(int jobId) {
        JobThread jobThread = jobThreadRepository.get(Integer.valueOf(jobId));
        return jobThread;
    }
}
