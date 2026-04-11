/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.dahua.panda.common.util.HttpRequestUtils
 *  javax.annotation.PostConstruct
 *  javax.servlet.http.HttpServletRequest
 *  lombok.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Service
 *  org.springframework.util.StringUtils
 */
package com.github.iappapp.panda.common.defense.csrf.service.impl;

import java.util.HashSet;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import com.github.iappapp.panda.utils.HttpRequestUtils;
import com.github.iappapp.panda.common.defense.config.DefenseConfigBuilder;
import com.github.iappapp.panda.common.defense.csrf.service.CsrfDetectionService;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class IpBasedCsrfDetectionServiceImpl
implements CsrfDetectionService {
    private static final Logger log = LoggerFactory.getLogger(IpBasedCsrfDetectionServiceImpl.class);
    private static final String URL_SEPARATOR = "//";
    private Set<String> securityIps = new HashSet<String>();
    private Set<String> securityUrls = new HashSet<String>();
    @Autowired
    private DefenseConfigBuilder defenseConfigBuilder;

    @PostConstruct
    protected void init() {
        this.securityIps = new HashSet<String>(this.defenseConfigBuilder.getTrustIps());
        this.securityUrls = new HashSet<String>(this.defenseConfigBuilder.getTrustUrls());
    }

    @Override
    public boolean isCsrfReq(@NonNull HttpServletRequest req) {
        if (req == null) {
            throw new NullPointerException("req is marked non-null but is null");
        }
        String url = req.getRequestURL().toString();
        String referer = req.getHeader("Referer");
        return this.isCsrfReq(url, referer);
    }

    @Override
    public boolean isCsrfReq(String url, String referer) {
        log.debug("URL:" + url);
        log.debug("Referer:" + referer);
        if (this.securityUrls.contains(url)) {
            return false;
        }
        if (referer == null) {
            return false;
        }
        if (!StringUtils.hasText((String)referer) || !referer.contains(URL_SEPARATOR)) {
            return true;
        }
        if (this.securityIps.contains(referer = HttpRequestUtils.getIp((String)referer))) {
            return false;
        }
        String ip = HttpRequestUtils.getIp((String)url);
        if (this.securityIps.contains(ip)) {
            return false;
        }
        return StringUtils.hasText((String)ip) && !ip.equals(referer);
    }
}

