/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.dahua.panda.base.spring.context.ApplicationContextHelper
 *  com.dahua.panda.base.spring.filter.AbstractBaseFilter
 *  javax.servlet.FilterChain
 *  javax.servlet.ServletException
 *  javax.servlet.ServletRequest
 *  javax.servlet.ServletResponse
 *  javax.servlet.http.HttpServletRequest
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.util.AntPathMatcher
 */
package com.github.iappapp.panda.common.defense.xss;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.github.iappapp.panda.common.defense.config.DefenseConfigBuilder;
import com.github.iappapp.panda.context.ApplicationContextHelper;
import com.github.iappapp.panda.filter.AbstractBaseFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.AntPathMatcher;

public class XssFilter
extends AbstractBaseFilter {
    private static final Logger log = LoggerFactory.getLogger(XssFilter.class);
    @Autowired
    private DefenseConfigBuilder defenseConfigBuilder;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    public XssFilter() {
        log.info("XssFilter init...");
    }

    private void checkFiled() {
        if (this.defenseConfigBuilder == null) {
            this.defenseConfigBuilder = (DefenseConfigBuilder) ApplicationContextHelper.getBean(DefenseConfigBuilder.class);
        }
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        this.checkFiled();
        HttpServletRequest httpServletRequest = (HttpServletRequest)request;
        if (this.defenseConfigBuilder.isXssEnableFlag()) {
            for (String whiteUrl : this.defenseConfigBuilder.getXssWhiteUrls()) {
                if (!this.antPathMatcher.match(whiteUrl, ((HttpServletRequest)request).getRequestURI().replaceAll(httpServletRequest.getContextPath(), ""))) continue;
                chain.doFilter(request, response);
                return;
            }
            XssRequestWrapper requestWrapper = new XssRequestWrapper(httpServletRequest);
            chain.doFilter((ServletRequest)requestWrapper, response);
            return;
        }
        chain.doFilter(request, response);
    }
}

