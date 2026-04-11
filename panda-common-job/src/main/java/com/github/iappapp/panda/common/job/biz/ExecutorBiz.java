package com.github.iappapp.panda.common.job.biz;


import com.github.iappapp.panda.common.job.biz.model.IdleBeatParam;
import com.github.iappapp.panda.common.job.biz.model.KillParam;
import com.github.iappapp.panda.common.job.biz.model.LogParam;
import com.github.iappapp.panda.common.job.biz.model.LogResult;
import com.github.iappapp.panda.common.job.biz.model.ReturnT;
import com.github.iappapp.panda.common.job.biz.model.TriggerParam;

public interface ExecutorBiz {
    ReturnT<String> beat();

    ReturnT<String> idleBeat(IdleBeatParam paramIdleBeatParam);

    ReturnT<String> run(TriggerParam paramTriggerParam);

    ReturnT<String> kill(KillParam paramKillParam);

    ReturnT<LogResult> log(LogParam paramLogParam);
}
