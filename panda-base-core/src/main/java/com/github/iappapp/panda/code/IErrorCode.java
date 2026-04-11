/*
 * Decompiled with CFR 0.152.
 */
package com.github.iappapp.panda.code;

import java.util.List;

public interface IErrorCode {
    String getCode();

    default List<String> getValue() {
        return null;
    }

    String getMessage();

    default String getSource() {
        return null;
    }

    default Integer getStatusCode() {
        return null;
    }
}

