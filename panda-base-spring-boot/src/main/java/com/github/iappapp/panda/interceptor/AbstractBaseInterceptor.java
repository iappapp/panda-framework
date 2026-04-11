/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.web.servlet.handler.HandlerInterceptorAdapter
 */
package com.github.iappapp.panda.interceptor;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public abstract class AbstractBaseInterceptor extends HandlerInterceptorAdapter {
    private static final Logger log = LoggerFactory.getLogger(AbstractBaseInterceptor.class);
    private static final String STATIC_SUFFIX = ".css,.js,.png,.jpg,.gif,.jpeg,.bmp,.ico,.swf,.psd,.htc,.htm,.html,.crx,.xpi,.exe,.ipa,.apk,.woff2,.ico,.swf,.ttf,.otf,.svg,.woff,.eot,.dwr,.json";
    private static final String[] STATIC_FILES = StringUtils.split(STATIC_SUFFIX, ",");
    public static final int LOG_FILTER_ORDER = 1;
    public static final int XSS_FILTER_ORDER = 10;
    public static final int CSRF_FILTER_ORDER = 20;
    public static final int TOKEN_FILTER_ORDER = 30;
    public static final int LICENSE_FILTER_ORDER = 40;

    public static boolean isStaticFile(String uri) {
        return StringUtils.endsWithAny(uri, STATIC_FILES);
    }
}

