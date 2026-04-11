package com.github.iappapp.panda.common.job.biz.model;

import java.io.Serializable;

public class LogParam implements Serializable {
    private static final long serialVersionUID = 42L;

    private long logDateTim;

    private long logId;

    private int fromLineNum;

    public LogParam() {
    }

    public LogParam(long logDateTim, long logId, int fromLineNum) {
        this.logDateTim = logDateTim;
        this.logId = logId;
        this.fromLineNum = fromLineNum;
    }

    public long getLogDateTim() {
        return this.logDateTim;
    }

    public void setLogDateTim(long logDateTim) {
        this.logDateTim = logDateTim;
    }

    public long getLogId() {
        return this.logId;
    }

    public void setLogId(long logId) {
        this.logId = logId;
    }

    public int getFromLineNum() {
        return this.fromLineNum;
    }

    public void setFromLineNum(int fromLineNum) {
        this.fromLineNum = fromLineNum;
    }
}
