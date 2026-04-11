package com.github.iappapp.panda.common.job.biz;


import com.github.iappapp.panda.common.job.biz.model.HandleCallbackParam;
import com.github.iappapp.panda.common.job.biz.model.JobGroupParam;
import com.github.iappapp.panda.common.job.biz.model.JobInfoParam;
import com.github.iappapp.panda.common.job.biz.model.RegistryParam;
import com.github.iappapp.panda.common.job.biz.model.ReturnT;

import java.util.List;

public interface AdminBiz {
    /**
     * 处理结果回调
     * @param paramList
     * @return
     */
    ReturnT<String> callback(List<HandleCallbackParam> paramList);

    /**
     * 执行机注册
     * @param paramRegistryParam
     * @return
     */
    ReturnT<String> registry(RegistryParam paramRegistryParam);

    /**
     * 执行机注册移除
     * @param paramRegistryParam
     * @return
     */
    ReturnT<String> registryRemove(RegistryParam paramRegistryParam);

    /**
     * 注册任务组
     * @param paramJobGroupParam
     * @return
     */
    ReturnT<String> registerJobGroup(JobGroupParam paramJobGroupParam);

    /**
     * 注册任务信息
     * @param paramJobInfoParam
     * @return
     */
    ReturnT<String> registerJobInfo(JobInfoParam paramJobInfoParam);

    /**
     * 登录获取cookie
     * @param username
     * @param password
     * @return
     */
    ReturnT<String> login(String username, String password);

    /**
     * 查询工作分组
     * @param appname
     * @return
     */
    ReturnT<JobGroupParam> findJobGroup(String appname);

    /**
     * 查询任务信息
     * @param appname
     * @param jobGroup
     * @param jobHandler
     * @param jobDesc
     * @return
     */
    ReturnT<JobInfoParam> findJobInfo(String appname, int jobGroup, String jobHandler, String jobDesc);

    /**
     * 更新任务信息
     * @param param
     * @return
     */
    ReturnT<String> updateJobInfo(JobInfoParam param);

    /**
     * 开始任务
     * @param jobId
     * @return
     */
    ReturnT<String> startJob(int jobId);

    /**
     * 结束任务
     * @param jobId
     * @return
     */
    ReturnT<String> stopJob(int jobId);

    /**
     * 开始任务
     * @param appname
     * @param jobHandler
     * @param jobDesc
     * @return
     */
    ReturnT<String> startJob(String appname, String jobHandler, String jobDesc);

    /**
     * 结束任务
     * @param appname
     * @param jobHandler
     * @param jobDesc
     * @return
     */
    ReturnT<String> stopJob(String appname, String jobHandler, String jobDesc);

    /**
     * 触发任务
     * @param appname
     * @param jobHandler
     * @param executorParam
     * @param jobDesc
     * @return
     */
    ReturnT<String> triggerJob(String appname, String jobHandler, String executorParam, String jobDesc);

    /**
     * 清理日志
     * @param jobGroup
     * @param jobId
     * @param type
     * @return
     */
    ReturnT<String> cleanLog(int jobGroup, int jobId, int type);

    /**
     * 移除任务
     * @param appname
     * @param jobHandler
     * @param jobDesc
     * @return
     */
    ReturnT<String> removeJob(String appname, String jobHandler, String jobDesc);

    /**
     * 查询任务信息
     * @param appname
     * @param cookie
     * @param jobGroup
     * @return
     */
    ReturnT<List<JobInfoParam>> findJobInfo(String appname, String cookie, int jobGroup);
}
