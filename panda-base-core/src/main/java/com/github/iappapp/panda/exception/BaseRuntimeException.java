/*
 * Decompiled with CFR 0.152.
 */
package com.github.iappapp.panda.exception;

import com.github.iappapp.panda.code.IErrorCode;

import java.util.Arrays;
import java.util.List;

public abstract class BaseRuntimeException extends RuntimeException implements IPandaException {
    private String errorCode;
    private List<String> value;
    private String errorMessage;
    private String source;
    private Integer statusCode;

    public BaseRuntimeException(IErrorCode iErrorCode) {
        super(iErrorCode.getMessage());
        this.errorCode = iErrorCode.getCode();
        this.value = iErrorCode.getValue();
        this.errorMessage = iErrorCode.getMessage();
        this.source = iErrorCode.getSource();
        this.statusCode = iErrorCode.getStatusCode();
    }

    public BaseRuntimeException(IErrorCode iErrorCode, Integer statusCode) {
        super(iErrorCode.getMessage());
        this.errorCode = iErrorCode.getCode();
        this.value = iErrorCode.getValue();
        this.errorMessage = iErrorCode.getMessage();
        this.source = iErrorCode.getSource();
        this.statusCode = statusCode;
    }

    public BaseRuntimeException(IErrorCode iErrorCode, String ... values) {
        super(iErrorCode.getMessage());
        this.errorCode = iErrorCode.getCode();
        this.errorMessage = iErrorCode.getMessage();
        this.source = iErrorCode.getSource();
        this.statusCode = iErrorCode.getStatusCode();
        this.value = Arrays.asList(values);
    }

    public BaseRuntimeException(IErrorCode iErrorCode, Throwable cause) {
        super(iErrorCode.getMessage(), cause);
        this.errorCode = iErrorCode.getCode();
        this.value = iErrorCode.getValue();
        this.errorMessage = iErrorCode.getMessage();
        this.statusCode = iErrorCode.getStatusCode();
        this.source = iErrorCode.getSource();
    }

    public BaseRuntimeException(String errorCode) {
        this.errorCode = errorCode;
    }

    public BaseRuntimeException(String errorCode, Integer statusCode) {
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }

    public BaseRuntimeException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public BaseRuntimeException(String errorCode, String errorMessage, Integer statusCode) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.statusCode = statusCode;
    }

    public BaseRuntimeException(String errorCode, List<String> value, String errorMessage, String source) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.value = value;
        this.errorMessage = errorMessage;
        this.source = source;
    }

    public BaseRuntimeException(String errorCode, List<String> value, String errorMessage, String source, Integer statusCode) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.value = value;
        this.errorMessage = errorMessage;
        this.source = source;
        this.statusCode = statusCode;
    }

    public BaseRuntimeException(String errorCode, String errorMessage, Throwable cause) {
        super(errorMessage, cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public BaseRuntimeException(String errorCode, String errorMessage, Integer statusCode, Throwable cause) {
        super(errorMessage, cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.statusCode = statusCode;
    }

    public BaseRuntimeException(String errorCode, List<String> value, String errorMessage, String source, Throwable cause) {
        super(errorMessage, cause);
        this.errorCode = errorCode;
        this.value = value;
        this.errorMessage = errorMessage;
        this.source = source;
    }

    public BaseRuntimeException(String errorCode, List<String> value, String errorMessage, String source, Integer statusCode, Throwable cause) {
        super(errorMessage, cause);
        this.errorCode = errorCode;
        this.value = value;
        this.errorMessage = errorMessage;
        this.source = source;
        this.statusCode = statusCode;
    }

    @Override
    public String getErrorCode() {
        return this.errorCode;
    }

    @Override
    public List<String> getValue() {
        return this.value;
    }

    @Override
    public String getErrorMessage() {
        return this.errorMessage;
    }

    @Override
    public String getSource() {
        return this.source;
    }

    @Override
    public Integer getStatusCode() {
        return this.statusCode;
    }

    @Override
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public void setValue(List<String> value) {
        this.value = value;
    }

    @Override
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }
}

