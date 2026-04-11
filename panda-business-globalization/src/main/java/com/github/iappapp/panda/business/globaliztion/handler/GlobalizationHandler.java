/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.util.StrUtil
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.lang.Nullable
 *  org.springframework.stereotype.Component
 */
package com.github.iappapp.panda.business.globaliztion.handler;

import java.util.Locale;
import javax.servlet.http.HttpServletRequest;

import com.github.iappapp.panda.business.globaliztion.cache.GlobalizationCache;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

public class GlobalizationHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalizationHandler.class);
    public static final String REQUEST_HEADER_LANGUAGE = "Accept-Language";
    public static final String REQUEST_HEADER_LANGUAGE_CUSTOM = "X-Operate-Lang";
    private static final String REQUEST_HEADER_LANGUAGE_SEPARATOR = "-";
    private static final String SEMICOLON = ";";
    private static final String COMMA = ",";
    @Autowired
    private GlobalizationCache globalizationCache;
    public static String env;

    public String translation(String entry, String language, boolean failReturnNull) {
        log.debug(":[{}], 翻译词条:[{}]", entry, language);
        if (StringUtils.isNotBlank(entry) && StringUtils.isNotBlank(language)) {
            entry = this.clearSpecialCharacters(entry);
            String translation = this.globalizationCache.getTranslation(entry, language);
            log.debug("[{}]---->{}", entry, translation);
            if (failReturnNull && translation.equals(entry)) {
                return null;
            }
            return translation;
        }
        log.error("国际化key或语言为空");
        if (failReturnNull) {
            return null;
        }
        return entry;
    }

    public String translation(String entry, String language) {
        return this.translation(entry, language, false);
    }

    public String translation(String entry, Locale locale) {
        String language = locale.getLanguage();
        log.debug("翻译词条:[{}], 翻译语言:[{}]", entry, language);
        return this.translation(entry, language);
    }

    private String clearSpecialCharacters(String entry) {
        entry = entry.replace("{", "");
        entry = entry.replace("}", "");
        return entry;
    }

    @Nullable
    public static String getLanguage(HttpServletRequest request) {
        String[] split;
        String customLanguage = request.getHeader(REQUEST_HEADER_LANGUAGE_CUSTOM);
        String language = request.getHeader(REQUEST_HEADER_LANGUAGE);
        if (StringUtils.isBlank(customLanguage) && StringUtils.isBlank(language)) {
            log.error("getLanguage Accept-Language and X-Operate-Lang head is null");
            return null;
        }
        if (StringUtils.isNotBlank(customLanguage)) {
            language = customLanguage;
        }
        String[] split1 = language.split(SEMICOLON);
        if ("overseas".equals(env)) {
            split = split1[0].split(COMMA);
        } else {
            String[] split2 = split1[0].split(REQUEST_HEADER_LANGUAGE_SEPARATOR);
            split = split2[0].split(COMMA);
        }
        if (StringUtils.isNotBlank(split[0])) {
            return split[0];
        }
        log.error("getLanguage language.split is null,language={}", language);
        return null;
    }
}

