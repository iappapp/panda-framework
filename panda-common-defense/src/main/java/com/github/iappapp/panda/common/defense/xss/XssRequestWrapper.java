/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.collection.CollectionUtil
 *  cn.hutool.core.io.resource.ResourceUtil
 *  com.alibaba.fastjson.JSON
 *  javax.servlet.ReadListener
 *  javax.servlet.ServletInputStream
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletRequestWrapper
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang.StringEscapeUtils
 *  org.owasp.validator.html.AntiSamy
 *  org.owasp.validator.html.CleanResults
 *  org.owasp.validator.html.Policy
 *  org.owasp.validator.html.PolicyException
 *  org.owasp.validator.html.ScanException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.util.StringUtils
 */
package com.github.iappapp.panda.common.defense.xss;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import com.alibaba.fastjson.JSON;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public class XssRequestWrapper extends HttpServletRequestWrapper {
    private static final Logger log = LoggerFactory.getLogger(XssRequestWrapper.class);
    private static Policy policy = null;
    private final Map<String, String[]> paramMap = new HashMap<String, String[]>();
    private byte[] content;
    private BufferedReader reader;
    private ServletInputStream inputStream;

    public XssRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        this.paramMap.putAll(request.getParameterMap());
        this.content = this.loadBody(request);
    }

    private static Boolean getResource(String resourcePath) {
        try {
            policy = Policy.getInstance(ResourceUtil.getStream(resourcePath));
            return true;
        }
        catch (PolicyException policyException) {
            log.error("XssRequestWrapper init is PolicyException", policyException);
        }
        catch (Exception exception) {
            log.error("XssRequestWrapper init is error", exception);
        }
        return false;
    }

    protected byte[] loadBody(HttpServletRequest request) throws IOException {
        this.content = IOUtils.toByteArray(request.getInputStream());
        this.inputStream = new RequestCachingInputStream(this.content);
        return this.content;
    }

    public String getParameter(String name) {
        String[] values;
        if (this.paramMap.containsKey(name) && (values = this.paramMap.get(name)) != null && values.length > 0) {
            return XssRequestWrapper.xssClean(values[0]);
        }
        return null;
    }

    public String getHeader(String name) {
        String header = super.getHeader(name);
        return StringUtils.hasLength(header) ? XssRequestWrapper.xssClean(header) : header;
    }

    public String[] getParameterValues(String name) {
        String[] values = this.paramMap.get(name);
        if (values != null && values.length > 0) {
            for (int i = 0; i < values.length; ++i) {
                values[i] = XssRequestWrapper.xssClean(values[i]);
            }
        }
        return values;
    }

    public Map<String, String[]> getParameterMap() {
        for (Map.Entry<String, String[]> o : this.paramMap.entrySet()) {
            String[] values = o.getValue();
            for (int i = 0; i < values.length; ++i) {
                values[i] = XssRequestWrapper.xssClean(values[i]);
            }
        }
        return this.paramMap;
    }

    public ServletInputStream getInputStream() throws IOException {
        return this.inputStream != null ? this.inputStream : super.getInputStream();
    }

    public BufferedReader getReader() throws IOException {
        if (this.reader == null) {
            this.reader = new BufferedReader(new InputStreamReader(this.getInputStream()));
        }
        return this.reader;
    }

    private static String xssClean(String taintedHtml) {
        try {
            AntiSamy antiSamy = new AntiSamy();
            CleanResults cr = antiSamy.scan(taintedHtml, policy);
            if (CollectionUtil.isEmpty(cr.getErrorMessages())) {
                return taintedHtml;
            }
            if (JSON.isValid(taintedHtml)) {
                String cleanStr = cr.getCleanHTML();
                cleanStr = StringEscapeUtils.unescapeHtml(cleanStr);
                log.info("xssClean json, original: {}, after: {}", taintedHtml, cleanStr);
                return JSON.toJSONString(JSON.parse(cleanStr));
            }
            String cleanStr = cr.getCleanHTML();
            if (!StringUtils.isEmpty((cleanStr = StringEscapeUtils.unescapeHtml(cleanStr)))) {
                cleanStr = cleanStr.replaceAll("\\\\n+", "");
            }
            log.info("xssClean text, original: {}, after: {}", taintedHtml, cleanStr);
            return cleanStr;
        }
        catch (ScanException scanException) {
            log.error("scan exception:", scanException);
        }
        catch (PolicyException policyException) {
            log.error("policy exception:", policyException);
        }
        catch (Exception exception) {
            log.error("xssClean exception:", exception);
        }
        return taintedHtml;
    }

    static {
        Boolean isSuccess = XssRequestWrapper.getResource("classpath:antisamy-tinymce.xml");
        if (!isSuccess.booleanValue()) {
            log.error("get resource failed from {}...", "classpath*:antisamy-tinymce.xml");
            XssRequestWrapper.getResource("classpath*:antisamy-tinymce.xml");
        }
    }

    private static class RequestCachingInputStream
    extends ServletInputStream {
        private final ByteArrayInputStream inputStream;

        public RequestCachingInputStream(byte[] bytes) throws IOException {
            String param = new String(bytes, StandardCharsets.UTF_8);
            byte[] cleanBytes = XssRequestWrapper.xssClean(param).getBytes(StandardCharsets.UTF_8);
            this.inputStream = new ByteArrayInputStream(cleanBytes);
        }

        public int read() throws IOException {
            return this.inputStream.read();
        }

        public boolean isFinished() {
            return this.inputStream.available() == 0;
        }

        public boolean isReady() {
            return true;
        }

        public void setReadListener(ReadListener readlistener) {
        }
    }
}

