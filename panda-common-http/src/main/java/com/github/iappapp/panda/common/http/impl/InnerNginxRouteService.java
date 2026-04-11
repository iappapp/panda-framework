/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.util.StrUtil
 *  com.alibaba.nacos.api.naming.NamingService
 *  com.alibaba.nacos.api.naming.pojo.Instance
 *  javax.annotation.Resource
 *  org.apache.commons.lang.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.github.iappapp.panda.common.http.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import java.net.URI;
import java.util.Optional;
import javax.annotation.Resource;

import com.github.iappapp.panda.common.http.config.HttpInnerNginxProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class InnerNginxRouteService {
    @Resource
    private HttpInnerNginxProperties httpInnerNginxProperties;

    @Autowired(required=false)
    private NamingService namingService;
    private static final String HTTP = "http";
    private static final String HTTPS = "https";

    public String dealRouteAddress(String url) {
        if (StrUtil.isBlank(url) || !this.httpInnerNginxProperties.isSupport()) {
            return url;
        }
        String authority = null;
        String address = null;
        try {
            URI uri = new URI(url);
            authority = uri.getAuthority();
            String scheme = uri.getScheme();
            Optional<Instance> ins = Optional.empty();
            if (HTTP.equals(scheme)) {
                ins = this.namingService.selectInstances(this.httpInnerNginxProperties.getServiceName(),
                        this.httpInnerNginxProperties.getGroupName(), true)
                        .stream().filter(instance -> instance.getPort() == this.httpInnerNginxProperties.getHttpPort())
                        .findAny();
            } else if (HTTPS.equals(scheme)) {
                ins = this.namingService.selectInstances(this.httpInnerNginxProperties.getServiceName(),
                        this.httpInnerNginxProperties.getGroupName(), true)
                        .stream().filter(instance -> instance.getPort() == this.httpInnerNginxProperties.getHttpsPort())
                        .findAny();
            }
            if (ins.isPresent()) {
                address = (ins.get()).toInetAddr();
            }
        }
        catch (Exception e) {
            log.error("getInnerNginx instance error", e);
        }
        if (StringUtils.isNotBlank(address) && StringUtils.isNotBlank(authority)) {
            url = url.replace(authority, address);
        }
        return url;
    }
}

