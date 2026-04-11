package com.github.iappapp.panda.common.job.handler.impl;

import com.github.iappapp.panda.common.job.context.PandaJobContext;
import com.github.iappapp.panda.common.job.context.PandaJobHelper;
import com.github.iappapp.panda.common.job.glue.GlueTypeEnum;
import com.github.iappapp.panda.common.job.handler.IJobHandler;
import com.github.iappapp.panda.common.job.log.PandaJobFileAppender;
import com.github.iappapp.panda.common.job.util.ScriptUtil;

import java.io.File;

/**
 * Created by xuxueli on 17/4/27.
 */
public class ScriptJobHandler extends IJobHandler {

    private int jobId;
    private long glueUpdatetime;
    private String gluesource;
    private GlueTypeEnum glueType;

    public ScriptJobHandler(int jobId, long glueUpdatetime, String gluesource, GlueTypeEnum glueType) {
        this.jobId = jobId;
        this.glueUpdatetime = glueUpdatetime;
        this.gluesource = gluesource;
        this.glueType = glueType;

        // clean old script file
        File glueSrcPath = new File(PandaJobFileAppender.getGlueSrcPath());
        if (glueSrcPath.exists()) {
            File[] glueSrcFileList = glueSrcPath.listFiles();
            if (glueSrcFileList != null && glueSrcFileList.length > 0) {
                for (File glueSrcFileItem : glueSrcFileList) {
                    if (glueSrcFileItem.getName().startsWith(jobId + "_")) {
                        glueSrcFileItem.delete();
                    }
                }
            }
        }

    }

    public long getGlueUpdatetime() {
        return glueUpdatetime;
    }

    @Override
    public void execute() throws Exception {

        if (!glueType.isScript()) {
            PandaJobHelper.handleFail("glueType[" + glueType + "] invalid.");
            return;
        }

        // cmd
        String cmd = glueType.getCmd();

        // make script file
        String scriptFileName = PandaJobFileAppender.getGlueSrcPath()
                .concat(File.separator)
                .concat(String.valueOf(jobId))
                .concat("_")
                .concat(String.valueOf(glueUpdatetime))
                .concat(glueType.getSuffix());
        File scriptFile = new File(scriptFileName);
        if (!scriptFile.exists()) {
            ScriptUtil.markScriptFile(scriptFileName, gluesource);
        }

        // log file
        String logFileName = PandaJobContext.getPandaJobContext().getJobLogFileName();

        // script params：0=param、1=分片序号、2=分片总数
        String[] scriptParams = new String[3];
        scriptParams[0] = PandaJobHelper.getJobParam();
        scriptParams[1] = String.valueOf(PandaJobContext.getPandaJobContext().getShardIndex());
        scriptParams[2] = String.valueOf(PandaJobContext.getPandaJobContext().getShardTotal());

        // invoke
        PandaJobHelper.log("script file:" + scriptFileName + " ");
        int exitValue = ScriptUtil.execToFile(cmd, scriptFileName, logFileName, scriptParams);

        if (exitValue == 0) {
            PandaJobHelper.handleSuccess();
        } else {
            PandaJobHelper.handleFail("script exit value(" + exitValue + ") is failed");
        }

    }

}
