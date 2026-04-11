package com.github.iappapp.panda.common.job.biz.client;


import com.github.iappapp.panda.common.job.biz.ExecutorBiz;
import com.github.iappapp.panda.common.job.biz.model.IdleBeatParam;
import com.github.iappapp.panda.common.job.biz.model.KillParam;
import com.github.iappapp.panda.common.job.biz.model.LogParam;
import com.github.iappapp.panda.common.job.biz.model.LogResult;
import com.github.iappapp.panda.common.job.biz.model.ReturnT;
import com.github.iappapp.panda.common.job.biz.model.TriggerParam;
import com.github.iappapp.panda.common.job.util.PandaJobRemotingUtil;

public class ExecutorBizClient implements ExecutorBiz {
    private String addressUrl;

    private String accessToken;

    public ExecutorBizClient() {
    }

    public ExecutorBizClient(String addressUrl, String accessToken) {
        this.addressUrl = addressUrl;
        this.accessToken = accessToken;
        if (!this.addressUrl.endsWith("/")) {
            this.addressUrl += "/";
        }
    }

    private int timeout = 3;

    public ReturnT<String> beat() {
        return PandaJobRemotingUtil.postBody(this.addressUrl + "beat",
                this.accessToken, this.timeout, "", String.class);
    }

    public ReturnT<String> idleBeat(IdleBeatParam idleBeatParam) {
        return PandaJobRemotingUtil.postBody(this.addressUrl + "idleBeat",
                this.accessToken, this.timeout, idleBeatParam, String.class);
    }

    public ReturnT<String> run(TriggerParam triggerParam) {
        return PandaJobRemotingUtil.postBody(this.addressUrl + "run",
                this.accessToken, this.timeout, triggerParam, String.class);
    }

    public ReturnT<String> kill(KillParam killParam) {
        return PandaJobRemotingUtil.postBody(this.addressUrl + "kill",
                this.accessToken, this.timeout, killParam, String.class);
    }

    public ReturnT<LogResult> log(LogParam logParam) {
        return PandaJobRemotingUtil.postBody(this.addressUrl + "log",
                this.accessToken, this.timeout, logParam, LogResult.class);
    }
}
