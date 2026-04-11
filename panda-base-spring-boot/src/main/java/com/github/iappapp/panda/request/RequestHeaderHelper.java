/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.net.NetUtil
 *  com.dahua.panda.common.util.HttpRequestUtils
 *  com.dahua.panda.common.util.ProcessUtils
 *  javax.annotation.PostConstruct
 *  org.apache.commons.lang.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Value
 *  org.springframework.stereotype.Component
 */
package com.github.iappapp.panda.request;

import javax.annotation.PostConstruct;

import cn.hutool.core.util.NetUtil;
import com.github.iappapp.panda.utils.HttpRequestUtils;
import com.github.iappapp.panda.utils.ProcessUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RequestHeaderHelper {
    private static final Logger log = LoggerFactory.getLogger(RequestHeaderHelper.class);
    private String localIp;
    private String appName;
    @Value(value="${request.server.name}")
    private String requestServerName;

    @PostConstruct
    public void init() {
        this.localIp = NetUtil.getLocalhostStr();
        if (StringUtils.isEmpty(this.localIp)) {
            this.localIp = "127.0.0.1";
        }
        this.appName = this.requestServerName + ":" + this.localIp + ":" + ProcessUtils.getServerPid();
    }

    public String getRequestId() {
        return HttpRequestUtils.generateRequestId(this.localIp);
    }

    public String getAppName() {
        return this.appName;
    }

    public String getLocalIp() {
        return this.localIp;
    }
}

