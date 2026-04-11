package com.github.iappapp.panda.common.job.enums;

public enum TriggerStatusEnum {
    STATUS_OPEN(1),
    STATUS_CLOSE(0);

    private int code;

    TriggerStatusEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }
}
