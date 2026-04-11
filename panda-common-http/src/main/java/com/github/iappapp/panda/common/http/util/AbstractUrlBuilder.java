/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.dahua.panda.common.util.UrlUtils
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.github.iappapp.panda.common.http.util;

import com.github.iappapp.panda.common.http.config.HttpClientProperties;
import com.github.iappapp.panda.utils.UrlUtils;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractUrlBuilder {
    @Autowired
    private HttpClientProperties httpClientProperties;

    public abstract String getServiceIpPort();

    public String getServiceHttpUrl(String uri) {
        String ipHost = this.getServiceIpPort();
        return this.getProtocol() + ipHost + uri;
    }

    public String getServiceHttpUrl(String uri, Object paramObj) {
        String url = this.getServiceHttpUrl(uri);
        return UrlUtils.urlWithParam(url, paramObj);
    }

    public String getProtocol() {
        if (this.httpClientProperties.isEnable()) {
            return "https://";
        }
        return "http://";
    }
}

