/*
 * Decompiled with CFR 0.152.
 */
package com.github.iappapp.panda.exception;

import java.util.List;

public interface IPandaException {
    public String getErrorCode();

    public String getErrorMessage();

    public void setErrorCode(String errorCode);

    public void setErrorMessage(String errorMessage);

    public List<String> getValue();

    public void setValue(List<String> value);

    public String getSource();

    public void setSource(String source);

    public Integer getStatusCode();

    public void setStatusCode(Integer statusCode);
}

