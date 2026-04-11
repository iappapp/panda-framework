package com.github.iappapp.panda.common.job.biz.model;

import java.io.Serializable;

public class LogResult implements Serializable {
    private static final long serialVersionUID = 42L;

    private int fromLineNum;

    private int toLineNum;

    private String logContent;

    private boolean isEnd;

    public LogResult() {
    }

    public LogResult(int fromLineNum, int toLineNum, String logContent, boolean isEnd) {
        this.fromLineNum = fromLineNum;
        this.toLineNum = toLineNum;
        this.logContent = logContent;
        this.isEnd = isEnd;
    }

    public int getFromLineNum() {
        return this.fromLineNum;
    }

    public void setFromLineNum(int fromLineNum) {
        this.fromLineNum = fromLineNum;
    }

    public int getToLineNum() {
        return this.toLineNum;
    }

    public void setToLineNum(int toLineNum) {
        this.toLineNum = toLineNum;
    }

    public String getLogContent() {
        return this.logContent;
    }

    public void setLogContent(String logContent) {
        this.logContent = logContent;
    }

    public boolean isEnd() {
        return this.isEnd;
    }

    public void setEnd(boolean end) {
        this.isEnd = end;
    }
}
