/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.boot.context.properties.ConfigurationProperties
 *  org.springframework.stereotype.Component
 *  org.springframework.util.StringUtils
 */
package com.github.iappapp.panda.business.globaliztion.config;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.iappapp.panda.business.globaliztion.handler.GlobalizationHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@ConfigurationProperties(value="panda.globalization")
@ToString
@EqualsAndHashCode
public class GlobalizationConfigBuilder {
    private static final String DEFAULT_LANGUAGE_LIST = "zh,en";
    private String language;
    private String env;
    private String filePath = "/cloud/dahua/i18n";
    private String appName;
    private Set<String> languageList = new HashSet<String>();
    private final LanguageCache cache = new LanguageCache();

    public void setEnv(String env) {
        this.env = env;
        GlobalizationHandler.env = env;
    }

    public void setLanguageList(String[] languageList) {
        if (languageList == null || languageList.length < 1) {
            languageList = new String[]{DEFAULT_LANGUAGE_LIST};
        }
        Set<String> arr = new HashSet<>(Arrays.asList(languageList))
                .stream()
                .filter(s -> !StringUtils.isEmpty(s.trim()))
                .collect(Collectors.toSet());
        this.languageList = arr;
    }

    public String getLanguage() {
        return this.language;
    }

    public String getEnv() {
        return this.env;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public String getAppName() {
        return this.appName;
    }

    public Set<String> getLanguageList() {
        return this.languageList;
    }

    public LanguageCache getCache() {
        return this.cache;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    @Data
    public class LanguageCache {
        private long expireTime = -1L;
    }
}

