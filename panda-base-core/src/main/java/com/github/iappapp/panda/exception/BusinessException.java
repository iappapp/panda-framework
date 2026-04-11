/*
 * Decompiled with CFR 0.152.
 */
package com.github.iappapp.panda.exception;

import com.github.iappapp.panda.code.IErrorCode;

import java.util.List;

public class BusinessException extends BaseRuntimeException {
    public BusinessException(IErrorCode iErrorCode) {
        super(iErrorCode);
    }

    public BusinessException(IErrorCode iErrorCode, Integer statusCode) {
        super(iErrorCode, statusCode);
    }

    public BusinessException(IErrorCode iErrorCode, String ... values) {
        super(iErrorCode, values);
    }

    public BusinessException(IErrorCode iErrorCode, Throwable cause) {
        super(iErrorCode, cause);
    }

    public BusinessException(String errorCode) {
        super(errorCode);
    }

    public BusinessException(String errorCode, Integer statusCode) {
        super(errorCode, statusCode);
    }

    public BusinessException(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

    public BusinessException(String errorCode, String errorMessage, Integer statusCode) {
        super(errorCode, errorMessage, statusCode);
    }

    public BusinessException(String errorCode, String errorMessage, Throwable cause) {
        super(errorCode, errorMessage, cause);
    }

    public BusinessException(String errorCode, String errorMessage, Integer statusCode, Throwable cause) {
        super(errorCode, errorMessage, statusCode, cause);
    }

    public BusinessException(String errorCode, List<String> value, String errorMessage, String source) {
        super(errorCode, value, errorMessage, source);
    }

    public BusinessException(String errorCode, List<String> value, String errorMessage, String source, Integer statusCode) {
        super(errorCode, value, errorMessage, source, statusCode);
    }

    public BusinessException(String errorCode, List<String> value, String errorMessage, String source, Throwable cause) {
        super(errorCode, value, errorMessage, source, cause);
    }

    public BusinessException(String errorCode, List<String> value, String errorMessage, String source, Integer statusCode, Throwable cause) {
        super(errorCode, value, errorMessage, source, statusCode, cause);
    }
}

