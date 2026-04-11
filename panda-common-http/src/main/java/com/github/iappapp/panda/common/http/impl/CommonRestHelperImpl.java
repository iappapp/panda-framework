/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Resource
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.slf4j.MDC
 *  org.springframework.http.HttpEntity
 *  org.springframework.http.HttpMethod
 *  org.springframework.http.ResponseEntity
 *  org.springframework.stereotype.Component
 *  org.springframework.util.MultiValueMap
 *  org.springframework.web.client.RestTemplate
 */
package com.github.iappapp.panda.common.http.impl;

import java.lang.reflect.Type;
import javax.annotation.Resource;

import com.github.iappapp.panda.common.http.CommonRestHelper;
import com.github.iappapp.panda.common.http.model.RestResponseDTO;
import com.github.iappapp.panda.common.http.util.ReqRespFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class CommonRestHelperImpl implements CommonRestHelper {
    private static final Logger log = LoggerFactory.getLogger(CommonRestHelperImpl.class);
    @Resource(name="pandaRestTemplate")
    private RestTemplate pandaRestTemplate;
    @Resource
    private InnerNginxRouteService innerNginxRouteService;

    @Override
    public <T> RestResponseDTO<T> doRest(HttpMethod method, String url,
                                         Object param, MultiValueMap<String, String> baseHeader, Type type) {
        log.info("request url:{} method:{} param:{}, requestId:{}", url, method, param, MDC.get("requestId"));
        url = this.innerNginxRouteService.dealRouteAddress(url);
        HttpEntity<Object> entity = new HttpEntity<>(param, baseHeader);
        long startTimeNanos = System.nanoTime();
        ResponseEntity<Object> responseEntity = this.pandaRestTemplate.exchange(url, method, entity, Object.class);
        return ReqRespFormatUtils.parseResponse(responseEntity, type, startTimeNanos);
    }
}

