/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.fastjson.JSON
 *  com.alibaba.fastjson.parser.Feature
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.slf4j.MDC
 *  org.springframework.http.ResponseEntity
 */
package com.github.iappapp.panda.common.http.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import java.lang.reflect.Type;

import com.github.iappapp.panda.common.http.model.RestResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;

public class ReqRespFormatUtils {
    private static final Logger log = LoggerFactory.getLogger(ReqRespFormatUtils.class);
    private static final String STRING_TYPE_NAME = "java.lang.String";
    private static final String OBJECT_TYPE_NAME = "java.lang.Object";

    public static <T> RestResponseDTO<T> parseResponse(ResponseEntity<Object> response, Type type, long startTimeNanos) {
        log.info("Request execution success, statusCode {} , cost {} ms, {}",
                response.getStatusCodeValue(), (System.currentTimeMillis() - startTimeNanos) / 1000000L, MDC.get("requestId"));
        RestResponseDTO<T> baseResponse = new RestResponseDTO<>();
        int status = response.getStatusCodeValue();
        baseResponse.setStatusCode(status);
        baseResponse.setSuccess(true);
        if (response.getBody() == null) {
            return baseResponse;
        }
        if (log.isDebugEnabled()) {
            log.debug("http response {}", JSON.toJSONString(response.getBody()));
        }
        if (OBJECT_TYPE_NAME.equals(type.getTypeName())) {
            baseResponse.setResult((T)response.getBody());
            return baseResponse;
        }
        if (STRING_TYPE_NAME.equals(type.getTypeName()) && response.getBody() instanceof String) {
            baseResponse.setResult((T)response.getBody());
            return baseResponse;
        }
        baseResponse.setResult(JSON.parseObject(JSON.toJSONString(response.getBody()), type, new Feature[0]));
        return baseResponse;
    }
}

