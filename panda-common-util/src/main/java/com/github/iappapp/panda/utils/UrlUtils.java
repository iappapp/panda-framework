/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.util.StrUtil
 *  cn.hutool.core.util.URLUtil
 *  cn.hutool.http.HttpUtil
 */
package com.github.iappapp.panda.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;

public class UrlUtils
extends URLUtil {
    public static String getParamValue(String url, String paramKey) {
        if (StrUtil.isBlank(url) || StrUtil.isBlank(paramKey)) {
            return null;
        }
        String[] urls = url.split("\\?");
        if (urls.length > 1) {
            for (String item : urls[1].split("&")) {
                String[] pairs = item.split("=");
                if (!paramKey.equals(pairs[0])) {
                    continue;
                }
                return pairs[1];
            }
        }
        return null;
    }

    public static String urlWithParam(String url, Object paramObj) {
        return UrlUtils.urlWithParam(url, paramObj, Charset.defaultCharset(), true);
    }

    public static String urlWithParam(String url, Object paramObj, Charset charset, boolean isEncodeParams) {
        Map map = paramObj instanceof Map ?
                (Map)paramObj : BeanUtils.beanToMap(paramObj, false, true);
        return HttpUtil.urlWithForm(url, map, charset, isEncodeParams);
    }

    public static InputStream getStream(String url) {
        InputStream inputStream;
        if (StrUtil.isBlank(url)) {
            return null;
        }
        try {
            inputStream = URLUtil.url(url).openStream();
        }
        catch (IOException e) {
            return null;
        }
        return inputStream;
    }
}

