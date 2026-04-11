package com.github.iappapp.panda.common.job.biz.model;

import java.io.Serializable;

public class HandleCallbackParam implements Serializable {
    private static final long serialVersionUID = 42L;

    private long logId;

    private long logDateTim;

    private int handleCode;

    private String handleMsg;

    public HandleCallbackParam() {
    }

    public HandleCallbackParam(long logId, long logDateTim, int handleCode, String handleMsg) {
        this.logId = logId;
        this.logDateTim = logDateTim;
        this.handleCode = handleCode;
        this.handleMsg = handleMsg;
    }

    public long getLogId() {
        return this.logId;
    }

    public void setLogId(long logId) {
        this.logId = logId;
    }

    public long getLogDateTim() {
        return this.logDateTim;
    }

    public void setLogDateTim(long logDateTim) {
        this.logDateTim = logDateTim;
    }

    public int getHandleCode() {
        return this.handleCode;
    }

    public void setHandleCode(int handleCode) {
        this.handleCode = handleCode;
    }

    public String getHandleMsg() {
        return this.handleMsg;
    }

    public void setHandleMsg(String handleMsg) {
        this.handleMsg = handleMsg;
    }

    public String toString() {
        return "HandleCallbackParam{logId=" + this.logId + ", logDateTim=" + this.logDateTim + ", handleCode=" + this.handleCode + ", handleMsg='" + this.handleMsg + '\'' + '}';
    }
}
