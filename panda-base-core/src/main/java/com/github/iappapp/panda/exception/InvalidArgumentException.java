/*
 * Decompiled with CFR 0.152.
 */
package com.github.iappapp.panda.exception;

import com.github.iappapp.panda.code.IErrorCode;

public class InvalidArgumentException extends BaseRuntimeException {
    public InvalidArgumentException(IErrorCode iErrorCode) {
        super(iErrorCode);
    }

    public InvalidArgumentException(IErrorCode iErrorCode, Throwable cause) {
        super(iErrorCode, cause);
    }

    public InvalidArgumentException(String errorCode) {
        super(errorCode);
    }

    public InvalidArgumentException(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

    public InvalidArgumentException(String errorCode, String errorMessage, Throwable cause) {
        super(errorCode, errorMessage, cause);
    }
}

