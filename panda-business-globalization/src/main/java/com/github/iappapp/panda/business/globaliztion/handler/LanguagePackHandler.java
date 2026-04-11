/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.lang.Nullable
 *  org.springframework.stereotype.Component
 */
package com.github.iappapp.panda.business.globaliztion.handler;

import java.util.Locale;
import java.util.Properties;

import com.github.iappapp.panda.business.globaliztion.config.GlobalizationConfigBuilder;
import com.github.iappapp.panda.business.globaliztion.util.PropertiesLoader;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

public class LanguagePackHandler {
    private static final Logger log = LoggerFactory.getLogger(LanguagePackHandler.class);
    private static final String SEPARATOR = "/";
    private static final String PROPERTIES_STR = ".properties";
    @Autowired
    private GlobalizationConfigBuilder globalizationConfigBuilder;
    private static String defaultLanguage;

    @Nullable
    public Properties getTranslationProperties() {
        defaultLanguage = this.globalizationConfigBuilder.getLanguage();
        if (StringUtils.isBlank(defaultLanguage)) {
            defaultLanguage = Locale.getDefault().getLanguage();
        }
        return this.getTranslationProperties(defaultLanguage);
    }

    @Nullable
    public Properties getTranslationProperties(String language) {
        if (StringUtils.isBlank(language)) {
            language = defaultLanguage;
        }
        StringBuffer path = new StringBuffer(this.globalizationConfigBuilder.getFilePath());
        path.append(SEPARATOR).append(language)
                .append(SEPARATOR)
                .append(this.globalizationConfigBuilder.getAppName())
                .append(SEPARATOR)
                .append(language)
                .append(PROPERTIES_STR);
        log.info("language package path is {}", path);
        try {
            return new PropertiesLoader(path.toString()).getProperties();
        }
        catch (Exception e) {
            log.error("getTranslationProperties is error,path={}", path, e);
            return null;
        }
    }
}

