/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.util.IdUtil
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang.StringUtils
 */
package com.github.iappapp.panda.utils;

import cn.hutool.core.util.IdUtil;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;

public class HttpRequestUtils {
    public static final String DOUBLE_SLASH = "//";
    public static final String COLON = ":";

    public static String getHost(HttpServletRequest req) {
        return HttpRequestUtils.getHost(req.getRequestURL().toString());
    }

    public static String getHost(String url) {
        if (StringUtils.isEmpty(url) || !url.contains(DOUBLE_SLASH)) {
            return null;
        }
        return url.split(DOUBLE_SLASH)[1].split("/")[0];
    }

    public static String getIp(HttpServletRequest req) {
        String host = HttpRequestUtils.getHost(req);
        return HttpRequestUtils.getIpByHost(host);
    }

    public static String getPort(HttpServletRequest req) {
        String host = HttpRequestUtils.getHost(req);
        return HttpRequestUtils.getIpByHost(host);
    }

    public static String getIpByHost(String host) {
        if (null != host && host.contains(COLON)) {
            return host.split(COLON)[0];
        }
        return null;
    }

    public static String getPortByHost(String host) {
        String[] str;
        if (null != host
                && host.contains(COLON)
                && (str = host.split(COLON)) != null
                && str.length > 1) {
            return str[1];
        }
        return null;
    }

    public static String getIp(String url) {
        String host = HttpRequestUtils.getHost(url);
        if (null != host && host.contains(COLON)) {
            host = host.split(COLON)[0];
        }
        return host;
    }

    public static String generateRequestId(String ip) {
        if (StringUtils.isNotEmpty(ip)) {
            String[] ips = ip.split("\\.");
            StringBuilder x = new StringBuilder();
            for (String signalIp : ips) {
                Integer iip = Integer.parseInt(signalIp);
                String hex = Integer.toHexString(iip);
                hex = hex.length() < 2 ? "0" + hex : hex;
                x.append(hex);
            }
            String uuid = IdUtil.randomUUID().toLowerCase().replaceAll("-", "");
            return x + "-" + uuid;
        }
        return IdUtil.randomUUID().toLowerCase().replaceAll("-", "");
    }

    public static String getIpAddr(HttpServletRequest request) {
        String ipAddress = null;
        try {
            ipAddress = request.getHeader("x-forwarded-for");
            if (StringUtils.isEmpty(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if (StringUtils.isEmpty(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if ((StringUtils.isEmpty(ipAddress) || "unknown".equalsIgnoreCase(ipAddress))
                    && "127.0.0.1".equals(ipAddress = request.getRemoteAddr())) {
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                }
                catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                if (inet != null) {
                    ipAddress = inet.getHostAddress();
                }
            }
            if (ipAddress != null && ipAddress.length() > 15 && ipAddress.indexOf(",") > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }
        catch (Exception e) {
            ipAddress = "";
        }
        return ipAddress;
    }
}

