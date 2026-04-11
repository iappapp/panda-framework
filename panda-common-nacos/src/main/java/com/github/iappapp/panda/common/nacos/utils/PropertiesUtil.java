/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.github.iappapp.panda.common.nacos.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesUtil {
    private static final Logger log = LoggerFactory.getLogger(PropertiesUtil.class);

    public static Properties loadConfigFromString(String config) {
        Properties properties = new Properties();
        if (StringUtils.isNotBlank(config)) {
            ByteArrayInputStream stream = new ByteArrayInputStream(config.getBytes(Charset.forName("UTF-8")));
            try {
                properties.load(stream);
            }
            catch (IOException e) {
                log.error("load properties from string error!", e);
            }
            finally {
                try {
                    ((InputStream)stream).close();
                }
                catch (IOException e) {
                    log.error("close stream error!", e);
                }
            }
        }
        return properties;
    }
}

