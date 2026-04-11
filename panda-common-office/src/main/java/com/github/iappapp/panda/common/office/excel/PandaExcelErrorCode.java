/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.dahua.panda.base.core.code.IErrorCode
 */
package com.github.iappapp.panda.common.office.excel;

import com.github.iappapp.panda.code.IErrorCode;

public enum PandaExcelErrorCode implements IErrorCode {
    EMPTY_DATA("99001010", "excel export data is empty");

    private String code;
    private String message;

    private PandaExcelErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}

