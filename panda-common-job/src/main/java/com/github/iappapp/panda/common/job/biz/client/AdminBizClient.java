package com.github.iappapp.panda.common.job.biz.client;

import com.github.iappapp.panda.common.job.biz.AdminBiz;
import com.github.iappapp.panda.common.job.biz.model.HandleCallbackParam;
import com.github.iappapp.panda.common.job.biz.model.JobGroupParam;
import com.github.iappapp.panda.common.job.biz.model.JobInfoParam;
import com.github.iappapp.panda.common.job.biz.model.RegistryParam;
import com.github.iappapp.panda.common.job.biz.model.ReturnT;
import com.github.iappapp.panda.common.job.constant.Constant;
import com.github.iappapp.panda.common.job.util.GsonTool;
import com.github.iappapp.panda.common.job.util.PandaJobRemotingUtil;
import com.google.gson.reflect.TypeToken;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdminBizClient implements AdminBiz {
    private static final Logger logger = LoggerFactory.getLogger(AdminBizClient.class);

    private String addressUrl;

    private String accessToken;

    private int timeout = 6;

    private String cookie;

    private String username;

    private String password;

    public AdminBizClient() {
    }

    public AdminBizClient(String addressUrl, String accessToken, String username, String password) {
        this.addressUrl = addressUrl;
        this.accessToken = accessToken;
        this.username = username;
        this.password = password;
        if (!this.addressUrl.endsWith("/")) {
            this.addressUrl += "/";
        }
    }

    private void tryLogin() {
        if (StringUtils.isEmpty(cookie)) {
            ReturnT<String> returnT = login(username, password);
            if (Objects.nonNull(returnT) && returnT.getCode() == 200) {
                this.cookie = returnT.getContent();
            }
        }
    }

    @Override
    public ReturnT<String> callback(List<HandleCallbackParam> callbackParamList) {
        return PandaJobRemotingUtil.postBody(this.addressUrl + "api/callback",
                this.accessToken, this.timeout, callbackParamList, String.class);
    }

    @Override
    public ReturnT<String> registry(RegistryParam registryParam) {
        return PandaJobRemotingUtil.postBody(this.addressUrl + "api/registry",
                this.accessToken, this.timeout, registryParam, String.class);
    }

    @Override
    public ReturnT<String> registryRemove(RegistryParam registryParam) {
        return PandaJobRemotingUtil.postBody(this.addressUrl + "api/registryRemove",
                this.accessToken, this.timeout, registryParam, String.class);
    }

    @Override
    public ReturnT<String> registerJobGroup(JobGroupParam jobGroupParam) {
        tryLogin();
        Map<String, String> paramMap = GsonTool.fromJson(
                GsonTool.toJson(jobGroupParam), new TypeToken<Map<String, String>>() {});
        String url = this.addressUrl + Constant.GROUP_ADD;
        String result = PandaJobRemotingUtil.post(url, cookie, paramMap, timeout);
        logger.info("panda-job registerJobGroup url={} \n paramMap={} \n result={}", url, paramMap, result);
        if (StringUtils.isEmpty(result)) {
            return ReturnT.FAIL;
        }
        return GsonTool.fromJson(result, new TypeToken<ReturnT<String>>(){});
    }

    @Override
    public ReturnT<String> registerJobInfo(JobInfoParam jobInfoParam) {
        tryLogin();
        jobInfoParam.setAlarmEmail("");
        Map<String, String> paramMap =
                GsonTool.fromJson(GsonTool.toJson(jobInfoParam), new TypeToken<Map<String, String>>(){});
        String url = this.addressUrl + Constant.JOB_ADD;
        String result = PandaJobRemotingUtil.post(url, this.cookie, paramMap, timeout);
        logger.info("panda-job registerJobInfo url={} \n paramMap={} \n result={}", url, paramMap, result);
        if (StringUtils.isEmpty(result)) {
            return ReturnT.FAIL;
        }
        return GsonTool.fromJson(result, new TypeToken<ReturnT<String>>(){});
    }

    @Override
    public synchronized ReturnT<String> login(String userName, String password) {
        if (this.cookie != null) {
            ReturnT<String> result = new ReturnT();
            result.setContent(this.cookie);
            result.setCode(200);
            return result;
        }
        try {
            String encodeUserName = URLEncoder.encode(userName, "UTF-8");
            String encodePassword = URLEncoder.encode(password, "UTF-8");
            ReturnT<String> returnT =
                    PandaJobRemotingUtil.post(this.addressUrl + "login", encodeUserName, encodePassword, timeout, String.class);
            boolean isRequestSucceeded = (returnT != null && returnT.getCode() == 200);
            boolean isContentNotEmpty = (returnT != null && returnT.getContent() != null && !(returnT.getContent()).isEmpty());
            if (isRequestSucceeded && isContentNotEmpty) {
                this.cookie = returnT.getContent();
                logger.info("panda-job login success cookie={}", returnT.getContent());
            } else {
                logger.error("panda-job login failed, please check username and password correct!");
            }
            return returnT;
        } catch (Exception ex) {
            logger.info("panda-job login fail ex={}", ex.getMessage());
            return ReturnT.FAIL;
        }
    }

    @Override
    public ReturnT<JobGroupParam> findJobGroup(String appname) {
        ReturnT<JobGroupParam> returnT = new ReturnT();

        tryLogin();
        Map<String, String> paramMap = new HashMap<>(4);
        paramMap.put("appname", appname);
        String url = this.addressUrl + Constant.GROUP_QUERY;
        String result = PandaJobRemotingUtil.post(url, this.cookie, paramMap, timeout);
        logger.info("panda-job findJobGroup url={} \n paramMap={} \n result={}", url, paramMap, result);
        Map<String, Object> dataMap = GsonTool.fromJson(result, new TypeToken<Map<String, Object>>() {});
        JobGroupParam jobGroupParam = null;
        if (dataMap != null && dataMap.containsKey("data")) {
            List<JobGroupParam> jobGroupParamList =
                    GsonTool.fromJson(GsonTool.toJson(dataMap.get("data")), new TypeToken<List<JobGroupParam>>() {});
            jobGroupParam = jobGroupParamList.stream()
                    .filter(d -> d.getAppname().equals(appname))
                    .findAny().orElse(null);
        }

        returnT.setCode(200);
        returnT.setContent(jobGroupParam);
        return returnT;
    }

    @Override
    public ReturnT<JobInfoParam> findJobInfo(String appname, int jobGroup, String jobHandler, String jobDesc) {
        tryLogin();
        Map<String, String> paramMap = new HashMap<>(4);
        paramMap.put("jobGroup", String.valueOf(jobGroup));
        paramMap.put("jobDesc", jobDesc);
        paramMap.put("triggerStatus", String.valueOf(-1));
        paramMap.put("start", "0");
        paramMap.put("length", "256");
        paramMap.put("executorHandler", jobHandler);
        String url = this.addressUrl + Constant.JOB_QUERY;
        String result = PandaJobRemotingUtil.post(url, cookie, paramMap, timeout);
        Map<String, Object> dataMap = GsonTool.fromJson(result, new TypeToken<Map<String, Object>>() {});
        logger.info("panda-job findJobInfo url={} \n param={} \n result={}", url, paramMap, result);
        JobInfoParam jobInfoParam = null;
        if (null != dataMap && dataMap.containsKey("data")) {
            List<JobInfoParam> jobGroupParamList =
                    GsonTool.fromJson(GsonTool.toJson(dataMap.get("data")), new TypeToken<List<JobInfoParam>>() {});
            if (!StringUtils.isEmpty(jobDesc)) {
                jobGroupParamList = jobGroupParamList.stream()
                        .filter(d -> jobDesc.equals(d.getJobDesc()))
                        .collect(Collectors.toList());
            }
            jobInfoParam = jobGroupParamList.stream()
                    .filter(d -> d.getExecutorHandler().equals(jobHandler))
                    .findAny().orElse(null);
        }
        ReturnT<JobInfoParam> returnT = new ReturnT();
        returnT.setCode(200);
        returnT.setContent(jobInfoParam);
        return returnT;
    }

    @Override
    public ReturnT<String> updateJobInfo(JobInfoParam param) {
        tryLogin();
        param.setAlarmEmail("");
        Map<String, String> paramMap =
                GsonTool.fromJson(GsonTool.toJson(param), new TypeToken<Map<String, String>>() {});
        String url = this.addressUrl + Constant.JOB_UPDATE;
        String result = PandaJobRemotingUtil.post(url, cookie, paramMap, timeout);
        logger.info("panda-job updateJobInfo url={} \n paramMap={} \n result={}", url, paramMap, result);
        if (StringUtils.isEmpty(result)) {
            return ReturnT.FAIL;
        }
        return GsonTool.fromJson(result, new TypeToken<ReturnT<String>>(){});
    }

    @Override
    public ReturnT<String> startJob(int jobId) {
        tryLogin();
        String url = this.addressUrl + Constant.JOB_START;
        Map<String, String> paramMap = new HashMap<>(2);
        paramMap.put("id", String.valueOf(jobId));
        String result = PandaJobRemotingUtil.post(url, this.cookie, paramMap, timeout);
        logger.info("panda-job startJob url={} \n paramMap={} \n result={}", url, paramMap, result);
        if (StringUtils.isEmpty(result)) {
            return ReturnT.FAIL;
        }
        return GsonTool.fromJson(result, new TypeToken<ReturnT<String>>(){});
    }

    @Override
    public ReturnT<String> stopJob(int jobId) {
        String url = this.addressUrl + Constant.JOB_STOP;
        Map<String, String> paramMap = new HashMap<>(2);
        paramMap.put("id", String.valueOf(jobId));
        String result = PandaJobRemotingUtil.post(url, this.cookie, paramMap, timeout);
        logger.info("panda-job stopJob url={} \n paramMap={} \n result={}", url, paramMap, result);
        if (StringUtils.isEmpty(result)) {
            return ReturnT.FAIL;
        }
        return GsonTool.fromJson(result, new TypeToken<ReturnT<String>>(){});
    }

    @Override
    public ReturnT<String> startJob(String appname, String jobHandler, String jobDesc) {
        ReturnT<JobGroupParam> jobGroupResult = findJobGroup(appname);
        if (null == jobGroupResult || null == jobGroupResult.getContent()) {
            return ReturnT.FAIL;
        }
        ReturnT<JobInfoParam> jobInfoResult =
                findJobInfo(appname, (jobGroupResult.getContent()).getId(), jobHandler, jobDesc);
        if (null == jobInfoResult || null == jobInfoResult.getContent()) {
            return ReturnT.FAIL;
        }
        return startJob((jobInfoResult.getContent()).getId());
    }

    @Override
    public ReturnT<String> stopJob(String appname, String jobHandler, String jobDesc) {
        ReturnT<JobGroupParam> jobGroupResult = findJobGroup(appname);
        if (null == jobGroupResult || null == jobGroupResult.getContent()) {
            return ReturnT.FAIL;
        }
        ReturnT<JobInfoParam> jobInfoResult =
                findJobInfo(appname, (jobGroupResult.getContent()).getId(), jobHandler, jobDesc);
        if (null == jobInfoResult || null == jobInfoResult.getContent()) {
            return ReturnT.FAIL;
        }
        return stopJob((jobInfoResult.getContent()).getId());
    }

    @Override
    public ReturnT<String> triggerJob(String appname, String jobHandler, String executorParam, String jobDesc) {
        ReturnT<JobGroupParam> jobGroupResult = findJobGroup(appname);
        if (null == jobGroupResult || null == jobGroupResult.getContent()) {
            return ReturnT.FAIL;
        }
        ReturnT<JobInfoParam> jobInfoResult =
                findJobInfo(appname, (jobGroupResult.getContent()).getId(), jobHandler, jobDesc);
        if (null == jobInfoResult || null == jobInfoResult.getContent()) {
            return ReturnT.FAIL;
        }
        String url = this.addressUrl + Constant.JOB_TRIGGER;
        Map<String, String> paramMap = new HashMap<>(2);
        paramMap.put("id", String.valueOf((jobInfoResult.getContent()).getId()));
        if (!StringUtils.isEmpty(executorParam)) {
            paramMap.put("executorParam", executorParam);
        }
        String result = PandaJobRemotingUtil.post(url, this.cookie, paramMap, timeout);
        logger.info("panda-job triggerJob url={} \n paramMap={} \n result={}", url, paramMap, result);
        if (StringUtils.isEmpty(result)) {
            return ReturnT.FAIL;
        }
        return GsonTool.fromJson(result, new TypeToken<ReturnT<String>>(){});
    }

    @Override
    public ReturnT<String> cleanLog(int jobGroup, int jobId, int type) {
        tryLogin();
        String url = this.addressUrl + Constant.JOB_CLEAN_LOG;
        Map<String, String> paramMap = new HashMap<>(4);
        paramMap.put("jobGroup", String.valueOf(jobGroup));
        paramMap.put("jobId", String.valueOf(jobId));
        paramMap.put("type", String.valueOf(type));
        String result = PandaJobRemotingUtil.post(url, this.cookie, paramMap, timeout);
        logger.info("panda-job clean log url={} \n paramMap={} \n result={}", url, paramMap, result);
        if (StringUtils.isEmpty(result)) {
            return ReturnT.FAIL;
        }
        return GsonTool.fromJson(result, new TypeToken<ReturnT<String>>(){});
    }

    @Override
    public ReturnT<String> removeJob(String appname, String jobHandler, String jobDesc) {
        ReturnT<JobGroupParam> jobGroupResult = findJobGroup(appname);
        if (null == jobGroupResult || null == jobGroupResult.getContent()) {
            return ReturnT.FAIL;
        }
        ReturnT<JobInfoParam> jobInfoResult =
                findJobInfo(appname, (jobGroupResult.getContent()).getId(), jobHandler, jobDesc);
        if (null == jobInfoResult || null == jobInfoResult.getContent()) {
            return ReturnT.SUCCESS;
        }
        String url = this.addressUrl + Constant.JOB_REMOVE;
        Map<String, String> paramMap = new HashMap<>(4);
        paramMap.put("id", String.valueOf((jobInfoResult.getContent()).getId()));
        String result = PandaJobRemotingUtil.post(url, this.cookie, paramMap, timeout);
        logger.info("panda-job removeJob url={} \n paramMap={} \n result={}", url, paramMap, result);
        if (StringUtils.isEmpty(result)) {
            return ReturnT.FAIL;
        }
        return GsonTool.fromJson(result, new TypeToken<ReturnT<String>>(){});
    }

    @Override
    public ReturnT<List<JobInfoParam>> findJobInfo(String appname, String cookie, int jobGroup) {
        tryLogin();
        Map<String, String> paramMap = new HashMap<>(4);
        paramMap.put("jobGroup", String.valueOf(jobGroup));
        paramMap.put("triggerStatus", String.valueOf(-1));
        paramMap.put("start", "0");
        paramMap.put("length", "256");
        String url = this.addressUrl + Constant.JOB_QUERY;
        String result = PandaJobRemotingUtil.post(url, cookie, paramMap, timeout);
        Map<String, Object> dataMap = GsonTool.fromJson(result, new TypeToken<Map<String, Object>>(){});
        logger.info("panda-job findJobInfo url={} \n param={} \n result={}", url, paramMap, result);
        List<JobInfoParam> jobGroupParamList = new ArrayList<>();
        if (null != dataMap && dataMap.containsKey("data")) {
            jobGroupParamList =
                    GsonTool.fromJson(GsonTool.toJson(dataMap.get("data")), new TypeToken<List<JobInfoParam>>() {});
        }
        ReturnT<List<JobInfoParam>> returnT = new ReturnT();
        returnT.setCode(200);
        returnT.setContent(jobGroupParamList);
        return returnT;
    }
}
