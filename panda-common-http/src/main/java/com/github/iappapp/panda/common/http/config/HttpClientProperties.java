/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.boot.context.properties.ConfigurationProperties
 */
package com.github.iappapp.panda.common.http.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(value="panda.https")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HttpClientProperties {
    private Boolean support = true;
    private boolean enable = false;
}

