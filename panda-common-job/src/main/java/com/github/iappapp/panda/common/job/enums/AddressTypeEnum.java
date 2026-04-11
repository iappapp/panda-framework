package com.github.iappapp.panda.common.job.enums;

public enum AddressTypeEnum {
    AUTO_REGISTER(0),
    MANUAL_REGISTER(1);

    private int code;

    public int getCode() {
        return this.code;
    }

    AddressTypeEnum(int code) {
        this.code = code;
    }
}
