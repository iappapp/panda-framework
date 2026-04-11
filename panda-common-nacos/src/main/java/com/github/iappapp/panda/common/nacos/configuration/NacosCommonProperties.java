/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.boot.context.properties.ConfigurationProperties
 *  org.springframework.stereotype.Component
 */
package com.github.iappapp.panda.common.nacos.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(value="panda.nacos")
@Data
public class NacosCommonProperties {
    /**
     *
     */
    private int configRetryTime = 3;
    /**
     *
     */
    private int configRetryInterval = 5;
    /**
     *
     */
    private Boolean configRetryLast = Boolean.FALSE;
    /**
     *
     */
    private String username = "nacos";
    /**
     *
     */
    private String password = "nacos";
    /**
     *
     */
    private boolean enable = true;
}

