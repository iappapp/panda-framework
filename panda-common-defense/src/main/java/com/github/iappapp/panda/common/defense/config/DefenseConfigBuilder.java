/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.nacos.api.config.annotation.NacosValue
 *  com.alibaba.nacos.spring.context.annotation.config.EnableNacosConfig
 *  org.springframework.beans.factory.annotation.Value
 *  org.springframework.stereotype.Component
 */
package com.github.iappapp.panda.common.defense.config;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class DefenseConfigBuilder {
    @Value(value="${panda.defense.xss.check.enable:true}")
    private boolean xssEnableFlag;
    @Value(value="${panda.defense.csrf.check.enable:true}")
    private boolean csrfEnableFlag;
    @Value(value="${panda.defense.security.trustIPs:127.0.0.1}")
    private List<String> trustIps;
    @Value(value="${panda.defense.security.trustURLs:127.0.0.1}")
    private List<String> trustUrls;
    @Value(value="${panda.defense.xss.white-urls:}")
    private List<String> xssWhiteUrls;
}

