package com.github.iappapp.panda.common.job.biz.model;

import java.io.Serializable;

public class KillParam implements Serializable {
    private static final long serialVersionUID = 42L;

    private int jobId;

    public KillParam() {
    }

    public KillParam(int jobId) {
        this.jobId = jobId;
    }

    public int getJobId() {
        return this.jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }
}
