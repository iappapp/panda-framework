/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.fastjson.JSON
 *  com.alibaba.nacos.api.config.annotation.NacosValue
 *  com.alibaba.nacos.spring.context.annotation.config.EnableNacosConfig
 *  javax.servlet.http.HttpServletRequest
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Value
 *  org.springframework.core.MethodParameter
 *  org.springframework.http.MediaType
 *  org.springframework.http.converter.HttpMessageConverter
 *  org.springframework.http.server.ServerHttpRequest
 *  org.springframework.http.server.ServerHttpResponse
 *  org.springframework.http.server.ServletServerHttpRequest
 *  org.springframework.lang.Nullable
 *  org.springframework.web.bind.annotation.ControllerAdvice
 *  org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice
 *  org.springframework.web.util.ContentCachingRequestWrapper
 */
package com.github.iappapp.panda.controlleradvice;

import com.alibaba.fastjson.JSON;
import java.nio.charset.Charset;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.web.util.ContentCachingRequestWrapper;

/**
 * 响应体控制层切面
 * @author tiger
 * @date 2025-06-26
 */
@ControllerAdvice
public class PandaLogResponseBodyAdvice implements ResponseBodyAdvice<Object> {
    private static final Logger log = LoggerFactory.getLogger(PandaLogResponseBodyAdvice.class);
    private static final String DEFAULT_CHARSET = "UTF-8";
    @Value(value="${panda.base.spring.log.return.length:1024}")
    private Integer rtnMsgLength;
    @Value(value="${panda.base.spring.log.return.black.url:}")
    private String logReturnBlackUrl;
    @Value(value="${panda.base.spring.log.return.enable:true}")
    private boolean logReturnEnable;

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }

    @Nullable
    @Override
    public Object beforeBodyWrite(@Nullable Object o, MethodParameter methodParameter,
                                  MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        if (!this.logReturnEnable) {
            return o;
        }
        String className = methodParameter.getDeclaringClass().getName();
        String methodName = null;
        methodName = null != methodParameter.getMethod() ? methodParameter.getMethod().getName() : "UNKNOWN";
        String reqUri = request.getMethod().name() + " " + request.getURI();
        String reqBody = this.getBody(request);
        if (reqBody != null) {
            reqBody = reqBody.length() > this.rtnMsgLength ? reqBody.substring(0, this.rtnMsgLength) : reqBody;
        }
        String logRtnMsg = "";
        if (!this.requestUriInLogReturnBlackUrl(reqUri) && this.rtnMsgLength > 0 && o != null) {
            logRtnMsg = o instanceof String ? o.toString() : JSON.toJSONString(o);
            if (logRtnMsg.length() > this.rtnMsgLength) {
                logRtnMsg = logRtnMsg.substring(0, this.rtnMsgLength);
            }
        }
        log.info("\n---BEGIN---\nRequri:{}\nParams:{}\nMethod:{}\nReturn:{}\n---END---\n",
                reqUri, reqBody, className + "." + methodName, logRtnMsg);
        return o;
    }

    private String getBody(ServerHttpRequest request) {
        try {
            HttpServletRequest r = ((ServletServerHttpRequest)request).getServletRequest();
            if (r instanceof ContentCachingRequestWrapper) {
                return new String(((ContentCachingRequestWrapper)r)
                        .getContentAsByteArray(), Charset.forName(DEFAULT_CHARSET));
            }
        }
        catch (Exception e) {
            log.error("", e);
        }
        return StringUtils.EMPTY;
    }

    private Boolean requestUriInLogReturnBlackUrl(String reqUri) {
        String[] uris;
        if (this.logReturnBlackUrl.equals("*")) {
            return Boolean.FALSE;
        }
        uris = this.logReturnBlackUrl.split(",");
        for (String uri : uris) {
            if (!reqUri.equals(uri)) {
                continue;
            }
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}

