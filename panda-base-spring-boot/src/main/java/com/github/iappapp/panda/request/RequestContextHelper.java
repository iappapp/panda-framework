/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.dahua.panda.base.core.exception.SystemException
 *  javax.servlet.http.HttpServletRequest
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.web.context.request.RequestContextHolder
 *  org.springframework.web.context.request.ServletRequestAttributes
 */
package com.github.iappapp.panda.request;

import java.util.Objects;
import java.util.TimeZone;
import javax.servlet.http.HttpServletRequest;

import com.github.iappapp.panda.exception.SystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class RequestContextHelper {
    private static final Logger log = LoggerFactory.getLogger(RequestContextHelper.class);

    public static HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributes =
                (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        if (request == null) {
            throw new SystemException("Current thread can not bind a HttpServletRequest");
        }
        return request;
    }

    public static String getToken() {
        return RequestContextHelper.getToken("X-Subject-Token");
    }

    public static String getToken(String headerName) {
        HttpServletRequest request = RequestContextHelper.getRequest();
        return request.getHeader(headerName);
    }

    public static String getRequestId() {
        return RequestContextHelper.getRequestId("X-LC-RequestId");
    }

    public static String getRequestId(String headerName) {
        HttpServletRequest request = RequestContextHelper.getRequest();
        return request.getHeader(headerName);
    }

    public static TimeZone getTimeZone() {
        HttpServletRequest request = null;
        try {
            request = RequestContextHelper.getRequest();
        }
        catch (SystemException systemException) {
            log.error("request is null, return default TimeZone", systemException);
        }
        TimeZone timeZone = null;
        if (request == null || (timeZone = (TimeZone)request.getAttribute("timezone")) == null) {
            return TimeZone.getDefault();
        }
        return timeZone;
    }

    public static String getRealRemoteAddr() {
        HttpServletRequest request = RequestContextHelper.getRequest();
        String addr = request.getHeader("x-forwarded-for");
        if (addr == null) {
            return request.getRemoteAddr();
        }
        return addr;
    }
}

