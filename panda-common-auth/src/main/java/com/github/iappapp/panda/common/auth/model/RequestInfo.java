package com.github.iappapp.panda.common.auth.model;

import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @author liush2
 * @date 2019/8/31 10:26
 * @remarks
 */
public class RequestInfo {
    private HttpServletRequest request;
    protected String ipHeaderName = null;
    private String url;
    private Cookie[] cookies;
    private String urlHostPrefix;


    public RequestInfo() {
    }

    public RequestInfo(HttpServletRequest request) {
        this.request = request;
        this.url = request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
        this.cookies = request.getCookies();
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public String getParameter(String key, String defaultValue) {
        if (request == null) {
            return null;
        }

        String value = request.getParameter(key);
        return StringUtils.isEmpty(value) ? defaultValue : value;
    }

    public String getParameter(String key) {
        if (request == null) {
            return null;
        }

        String value = request.getParameter(key);
        return value;
    }

    public Cookie getCookie(String cookieName) {
        if (this.cookies == null) {
            return null;
        } else {
            for (int i = 0; i < this.cookies.length; ++i) {
                if (this.cookies[i].getName().equals(cookieName)) {
                    return this.cookies[i];
                }
            }

            return null;
        }
    }

    public String getCookieValue(String cookieName, String defaultValue) {
        Cookie c = this.getCookie(cookieName);
        if (c == null) {
            return defaultValue;
        } else {
            String value = c.getValue();
            return value != null && value.trim().length() > 0 ? value : defaultValue;
        }
    }

    public String getHeader(String name, String defaultValue) {
        String value = this.request.getHeader(name);
        return value != null && value.trim().length() > 0 ? value.trim() : defaultValue;
    }

    public String getHeader(String name) {
        return this.getHeader(name, (String) null);
    }


}
