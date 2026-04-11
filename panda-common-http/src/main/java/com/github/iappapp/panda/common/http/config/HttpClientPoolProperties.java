/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.nacos.api.config.annotation.NacosValue
 *  com.alibaba.nacos.spring.context.annotation.config.EnableNacosConfig
 *  org.springframework.boot.context.properties.ConfigurationProperties
 */
package com.github.iappapp.panda.common.http.config;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(value="panda.http.pool")
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class HttpClientPoolProperties {
    private String charset = "UTF-8";

    private int maxTotalConnect = 256;

    private int maxConnectPerRoute = 256;

    private int connectTimeout = 30000;

    private int retryTimes = 3;

    private int connectionRequestTimeout = 200;

    private int keepAliveTimeOut = 40000;

    @Value(value="${panda.http.pool.socket-timeout:30000}")
    private int socketTimeout = 30000;

    private int idleTimeout = 30000;

    private int disableRedirectMaxTotalConnect = 5;
}

