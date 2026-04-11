/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.boot.context.properties.ConfigurationProperties
 */
package com.github.iappapp.panda.common.http.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(value="panda.http.inner.nginx")
@Data
public class HttpInnerNginxProperties {

    private boolean support = false;

    private String serviceName = "G-Nginx";

    private String groupName = "GCBB";

    private int httpPort = 8714;

    private int httpsPort = 8715;
}

