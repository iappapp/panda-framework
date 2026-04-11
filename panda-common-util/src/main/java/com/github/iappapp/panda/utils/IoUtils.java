/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.io.IoUtil
 *  cn.hutool.core.util.URLUtil
 */
package com.github.iappapp.panda.utils;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.URLUtil;
import java.io.IOException;
import java.io.InputStream;

public class IoUtils extends IoUtil {
    public static byte[] getBytes(String url) {
        try {
            return IoUtils.readBytes(URLUtil.url(url).openStream());
        }
        catch (IOException e) {
            return null;
        }
    }
}

