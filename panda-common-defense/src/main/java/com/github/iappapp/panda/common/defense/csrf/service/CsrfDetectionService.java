/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  lombok.NonNull
 */
package com.github.iappapp.panda.common.defense.csrf.service;

import javax.servlet.http.HttpServletRequest;
import lombok.NonNull;

public interface CsrfDetectionService {
    public boolean isCsrfReq(@NonNull HttpServletRequest httpServletRequest);

    public boolean isCsrfReq(String url, String referer);
}

