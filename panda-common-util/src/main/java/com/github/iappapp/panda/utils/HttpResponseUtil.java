package com.github.iappapp.panda.utils;

import org.apache.commons.lang.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * @author liush2
 * @date 2019/8/31 15:31
 * @remarks
 */


public class HttpResponseUtil {

    public HttpResponseUtil() {
    }

    public static void setCookie(HttpServletResponse response, String name, String value, String domain, String path, int expireSecond) {
        Cookie cookie = new Cookie(name, value);
        cookie.setSecure(false);
        cookie.setPath(path);
        cookie.setMaxAge(expireSecond);
        if (!StringUtils.isEmpty(domain)) {
            cookie.setDomain(domain);
        }

        response.addCookie(cookie);
    }

    public static void setCookie(HttpServletResponse response, String name, String value, String domain, String path) {
        setCookie(response, name, value, domain, path, -1);
    }

    public static void setCookie(HttpServletResponse response, String name, String value, String domain) {
        setCookie(response, name, value, domain, "/", -1);
    }

    public static void setCookie(HttpServletResponse response, String name, String value) {
        setCookie(response, name, value, (String)null, "/", -1);
    }

    public static void deleteCookie(HttpServletResponse response, Cookie cookie) {
        if (cookie != null) {
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }

    }

    public static void deleteCookie(HttpServletResponse response, String name) {
        setCookie(response, name, (String)null, (String)null, "/", 0);
    }

    public static void deleteCookie(HttpServletResponse response, String name, String domain) {
        setCookie(response, name, (String)null, domain, "/", 0);
    }

    public static void deleteCookie(HttpServletResponse response, String name, String domain, String path) {
        setCookie(response, name, (String)null, domain, path, 0);
    }

    public static void setCacheHeader(HttpServletResponse response, int expireSeconds, Date lastModified) throws IOException {
        response.setHeader("Cache-Control", "public, max-age=" + expireSeconds);
        if (lastModified != null) {
            response.setDateHeader("Last-Modified", lastModified.getTime());
        }

        response.setDateHeader("Expires", System.currentTimeMillis() + (long)expireSeconds * 1000L);
    }

    public static void cacheAndRedriect(HttpServletResponse response, int expireSeconds, String url) throws IOException {
        setCacheHeader(response, expireSeconds, new Date());
        response.sendRedirect(url);
    }

    public static void noCacheAndRedriect(HttpServletResponse response, String url) throws IOException {
        setNoCacheHeader(response);
        response.sendRedirect(url);
    }

    public static void setNoCacheHeader(HttpServletResponse response) throws IOException {
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "max-age=0");
        response.setHeader("Expires", "0");
    }
}
