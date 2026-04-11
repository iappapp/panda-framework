package com.github.iappapp.panda.utils;

import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author xiongth
 */
public class HttpRequestUtil {

    public static String getParams(HttpServletRequest request, String name, String defaultValue) {
        String value = request.getParameter(name);
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        return value;
    }

    public static String getReturnStr(int code, String err) {
        return "{\"code\":" + code + ",\"err\":" + err + "}";
    }

    public static String getReturn(int code, String err) {
        return "{\"code\":" + code + ",\"err\":\"" + err + "\"}";
    }

}
