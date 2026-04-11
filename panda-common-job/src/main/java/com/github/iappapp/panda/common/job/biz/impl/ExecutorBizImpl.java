package com.github.iappapp.panda.common.job.biz.impl;

import com.github.iappapp.panda.common.job.biz.ExecutorBiz;
import com.github.iappapp.panda.common.job.biz.model.IdleBeatParam;
import com.github.iappapp.panda.common.job.biz.model.KillParam;
import com.github.iappapp.panda.common.job.biz.model.LogParam;
import com.github.iappapp.panda.common.job.biz.model.LogResult;
import com.github.iappapp.panda.common.job.biz.model.ReturnT;
import com.github.iappapp.panda.common.job.biz.model.TriggerParam;
import com.github.iappapp.panda.common.job.enums.ExecutorBlockStrategyEnum;
import com.github.iappapp.panda.common.job.executor.PandaJobExecutor;
import com.github.iappapp.panda.common.job.glue.GlueFactory;
import com.github.iappapp.panda.common.job.glue.GlueTypeEnum;
import com.github.iappapp.panda.common.job.handler.IJobHandler;
import com.github.iappapp.panda.common.job.handler.impl.GlueJobHandler;
import com.github.iappapp.panda.common.job.handler.impl.ScriptJobHandler;
import com.github.iappapp.panda.common.job.log.PandaJobFileAppender;
import com.github.iappapp.panda.common.job.thread.JobThread;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecutorBizImpl implements ExecutorBiz {
    private static Logger logger = LoggerFactory.getLogger(ExecutorBizImpl.class);

    @Override
    public ReturnT<String> beat() {
        return ReturnT.SUCCESS;
    }

    @Override
    public ReturnT<String> idleBeat(IdleBeatParam idleBeatParam) {
        boolean isRunningOrHasQueue = false;
        JobThread jobThread = PandaJobExecutor.loadJobThread(idleBeatParam.getJobId());
        if (jobThread != null && jobThread.isRunningOrHasQueue()) {
            isRunningOrHasQueue = true;
        }
        if (isRunningOrHasQueue) {
            return new ReturnT(500, "job thread is running or has trigger queue.");
        }
        return ReturnT.SUCCESS;
    }

    @Override
    public ReturnT<String> run(TriggerParam triggerParam) {
        ScriptJobHandler scriptJobHandler = null;
        JobThread jobThread = PandaJobExecutor.loadJobThread(triggerParam.getJobId());
        IJobHandler jobHandler = (jobThread != null) ? jobThread.getHandler() : null;
        String removeOldReason = null;
        GlueTypeEnum glueTypeEnum = GlueTypeEnum.match(triggerParam.getGlueType());
        if (GlueTypeEnum.BEAN == glueTypeEnum) {
            IJobHandler newJobHandler = PandaJobExecutor.loadJobHandler(triggerParam.getExecutorHandler());
            if (jobThread != null && jobHandler != newJobHandler) {
                removeOldReason = "change jobhandler or glue type, and terminate the old job thread.";
                jobThread = null;
                jobHandler = null;
            }
            if (jobHandler == null) {
                jobHandler = newJobHandler;
                if (jobHandler == null) {
                    return new ReturnT(500, "job handler [" + triggerParam.getExecutorHandler() + "] not found.");
                }
            }
        } else if (GlueTypeEnum.GLUE_GROOVY == glueTypeEnum) {
            if (jobThread != null && (
                    !(jobThread.getHandler() instanceof GlueJobHandler) || ((GlueJobHandler) jobThread
                            .getHandler()).getGlueUpdatetime() != triggerParam.getGlueUpdatetime())) {
                removeOldReason = "change job source or glue type, and terminate the old job thread.";
                jobThread = null;
                jobHandler = null;
            }
            if (jobHandler == null) {
                try {
                    IJobHandler originJobHandler = GlueFactory.getInstance().loadNewInstance(triggerParam.getGlueSource());
                    GlueJobHandler glueJobHandler = new GlueJobHandler(originJobHandler, triggerParam.getGlueUpdatetime());
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    return new ReturnT(500, e.getMessage());
                }
            }
        } else if (glueTypeEnum != null && glueTypeEnum.isScript()) {
            if (jobThread != null && (
                    !(jobThread.getHandler() instanceof ScriptJobHandler) || ((ScriptJobHandler) jobThread
                            .getHandler()).getGlueUpdatetime() != triggerParam.getGlueUpdatetime())) {
                removeOldReason = "change job source or glue type, and terminate the old job thread.";
                jobThread = null;
                jobHandler = null;
            }
            if (jobHandler == null) {
                scriptJobHandler = new ScriptJobHandler(triggerParam.getJobId(), triggerParam.getGlueUpdatetime(), triggerParam.getGlueSource(), GlueTypeEnum.match(triggerParam.getGlueType()));
            }
        } else {
            return new ReturnT(500, "glueType[" + triggerParam.getGlueType() + "] is not valid.");
        }
        if (jobThread != null) {
            ExecutorBlockStrategyEnum blockStrategy = ExecutorBlockStrategyEnum.match(triggerParam.getExecutorBlockStrategy(), null);
            if (ExecutorBlockStrategyEnum.DISCARD_LATER == blockStrategy) {
                if (jobThread.isRunningOrHasQueue()) {
                    return new ReturnT(500, "block strategy effect：" + ExecutorBlockStrategyEnum.DISCARD_LATER.getTitle());
                }
            } else if (ExecutorBlockStrategyEnum.COVER_EARLY == blockStrategy) {
                if (jobThread.isRunningOrHasQueue()) {
                    removeOldReason = "block strategy effect：" + ExecutorBlockStrategyEnum.COVER_EARLY.getTitle();
                    jobThread = null;
                }
            }
        }
        if (jobThread == null) {
            jobThread = PandaJobExecutor.registJobThread(triggerParam.getJobId(), scriptJobHandler, removeOldReason);
        }
        ReturnT<String> pushResult = jobThread.pushTriggerQueue(triggerParam);
        return pushResult;
    }

    @Override
    public ReturnT<String> kill(KillParam killParam) {
        JobThread jobThread = PandaJobExecutor.loadJobThread(killParam.getJobId());
        if (jobThread != null) {
            PandaJobExecutor.removeJobThread(killParam.getJobId(), "scheduling center kill job.");
            return ReturnT.SUCCESS;
        }
        return new ReturnT(200, "job thread already killed.");
    }

    @Override
    public ReturnT<LogResult> log(LogParam logParam) {
        String logFileName = PandaJobFileAppender.makeLogFileName(new Date(logParam.getLogDateTim()), logParam.getLogId());
        LogResult logResult = PandaJobFileAppender.readLog(logFileName, logParam.getFromLineNum());
        return new ReturnT(logResult);
    }
}
