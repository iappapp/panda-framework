/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.dahua.panda.base.core.code.CommonErrorCode
 *  com.dahua.panda.base.core.code.IErrorCode
 *  com.dahua.panda.base.core.exception.SystemException
 *  com.dahua.panda.base.spring.interceptor.AbstractBaseInterceptor
 *  com.dahua.panda.base.spring.response.ResponseHelper
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  lombok.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.github.iappapp.panda.common.defense.csrf;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.iappapp.panda.code.CommonErrorCode;
import com.github.iappapp.panda.code.IErrorCode;
import com.github.iappapp.panda.common.defense.config.DefenseConfigBuilder;
import com.github.iappapp.panda.common.defense.csrf.service.CsrfDetectionService;
import com.github.iappapp.panda.exception.SystemException;
import com.github.iappapp.panda.interceptor.AbstractBaseInterceptor;
import com.github.iappapp.panda.response.ResponseHelper;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CsrfInterceptor
extends AbstractBaseInterceptor {
    private static final Logger log = LoggerFactory.getLogger(CsrfInterceptor.class);
    public static final String CSRF_ERROR = "FORBIDDEN REQUEST";
    @Autowired
    ResponseHelper responseHelper;
    @Autowired
    private CsrfDetectionService detection;
    @Autowired
    private DefenseConfigBuilder defenseConfigBuilder;

    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        if (request == null) {
            throw new NullPointerException("request is marked non-null but is null");
        }
        if (response == null) {
            throw new NullPointerException("response is marked non-null but is null");
        }
        if (handler == null) {
            throw new NullPointerException("handler is marked non-null but is null");
        }
        if (!this.defenseConfigBuilder.isCsrfEnableFlag()) {
            return true;
        }
        if (this.detection.isCsrfReq(request)) {
            log.debug("Csrf interceptor, url: {}, method: {}, params: {}", new Object[]{request.getRequestURL(), request.getMethod(), request.getParameterMap()});
            throw new SystemException((IErrorCode) CommonErrorCode.REQUEST_FORBIDDEN);
        }
        return true;
    }
}

