/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.annotation.WebFilter
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.slf4j.MDC
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.core.annotation.Order
 *  org.springframework.web.util.ContentCachingRequestWrapper
 */
package com.github.iappapp.panda.filter;

import java.io.IOException;
import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.iappapp.panda.request.RequestHeaderHelper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.web.util.ContentCachingRequestWrapper;

@Order(value=1)
@WebFilter(filterName="pandaLogFilter")
public class LogFilter extends AbstractBaseFilter {
    private static final Logger log = LoggerFactory.getLogger(LogFilter.class);
    private static final String MONITOR_URI = "/monitor/metrics";
    @Resource
    private RequestHeaderHelper requestHeaderHelper;

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)resp;
        String url = request.getRequestURI();
        if (StringUtils.isNotEmpty(url) && url.contains(MONITOR_URI)) {
            chain.doFilter(req, resp);
            return;
        }
        String requestId = request.getHeader("X-LC-RequestId");
        String appName = request.getHeader("X-App-Name");
        requestId = StringUtils.isEmpty(requestId) ? this.requestHeaderHelper.getRequestId() : requestId;
        appName = StringUtils.isEmpty(appName) ? this.requestHeaderHelper.getAppName() : appName;
        String resReqId = response.getHeader("X-LC-RequestId");
        String resAppName = response.getHeader("X-App-Name");
        if (StringUtils.isNotEmpty(requestId) && StringUtils.isEmpty(resReqId)) {
            response.addHeader("X-LC-RequestId", requestId);
            MDC.put("requestId", ("[requestId=" + requestId + "]"));
            if (StringUtils.isEmpty(resAppName)) {
                response.addHeader("X-App-Name", appName);
                MDC.put("appName", appName);
            }
            log.info("Enter[appName=" + appName + "]: " + request.getRequestURI());
        }
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        chain.doFilter(requestWrapper, resp);
    }
}

