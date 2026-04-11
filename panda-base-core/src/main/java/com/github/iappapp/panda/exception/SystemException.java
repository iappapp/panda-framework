/*
 * Decompiled with CFR 0.152.
 */
package com.github.iappapp.panda.exception;


import com.github.iappapp.panda.code.IErrorCode;

import java.util.List;

public class SystemException extends BaseRuntimeException {
    public SystemException(IErrorCode iErrorCode) {
        super(iErrorCode);
    }

    public SystemException(IErrorCode iErrorCode, Throwable cause) {
        super(iErrorCode, cause);
    }

    public SystemException(String errorCode) {
        super(errorCode);
    }

    public SystemException(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

    public SystemException(String errorCode, String errorMessage, Throwable cause) {
        super(errorCode, errorMessage, cause);
    }

    public SystemException(String errorCode, List<String> value, String errorMessage, String source) {
        super(errorCode, value, errorMessage, source);
    }

    public SystemException(String errorCode, List<String> value, String errorMessage, String source, Throwable cause) {
        super(errorCode, value, errorMessage, source, cause);
    }
}

