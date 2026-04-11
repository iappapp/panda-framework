/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.collection.CollectionUtil
 *  com.github.benmanes.caffeine.cache.Caffeine
 *  org.apache.commons.lang.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.boot.ApplicationArguments
 *  org.springframework.boot.ApplicationRunner
 *  org.springframework.cache.caffeine.CaffeineCache
 *  org.springframework.scheduling.annotation.Scheduled
 *  org.springframework.stereotype.Component
 */
package com.github.iappapp.panda.business.globaliztion.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.github.iappapp.panda.business.globaliztion.config.GlobalizationConfigBuilder;
import com.github.iappapp.panda.business.globaliztion.handler.LanguagePackHandler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

public class GlobalizationCache implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(GlobalizationCache.class);
    @Autowired
    private GlobalizationConfigBuilder globalizationConfigBuilder;
    @Autowired
    private LanguagePackHandler languagePackHandler;
    private Map<String, CaffeineCache> globalizationInfoCacheMap = new ConcurrentHashMap<String, CaffeineCache>();

    private Boolean checkCache(String language) {
        CaffeineCache globalizationInfoCache = this.globalizationInfoCacheMap.get(language);
        if (globalizationInfoCache == null) {
            return this.loadCache(language);
        }
        return true;
    }

    private Boolean checkCache() {
        return this.checkCache("zh");
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("国际化缓存加载 {}", this.globalizationConfigBuilder.getLanguageList());
        if (CollectionUtils.isNotEmpty(this.globalizationConfigBuilder.getLanguageList())) {
            this.globalizationConfigBuilder.getLanguageList().forEach(this::loadCache);
        }
    }

    @Scheduled(cron="${panda.globalization.cache.clear.cron:0 0 1 * * ?}")
    public void clearCache() {
        if (MapUtils.isNotEmpty(this.globalizationInfoCacheMap)) {
            this.globalizationInfoCacheMap.clear();
            this.run(null);
        }
    }

    public String getTranslation(String entry, String language) {
        Boolean loadCacheFlag = this.checkCache(language);
        String translateStr = null;
        CaffeineCache globalizationInfoCache = this.globalizationInfoCacheMap.get(language);
        if (loadCacheFlag) {
            translateStr = globalizationInfoCache.get(entry, String.class);
            if (StringUtils.isBlank(translateStr)) {
                log.error("[{}] 未翻译成功，请检查语言包", entry);
                translateStr = entry;
            }
        } else {
            log.error("无法加载到语言包，请检查语言包路径");
            translateStr = entry;
        }
        return translateStr;
    }

    private Boolean loadCache(String language) {
        Properties translationProperties;
        log.info("load language cache!");
        CaffeineCache globalizationInfoCache = this.globalizationInfoCacheMap.get(language);
        if (globalizationInfoCache != null) {
            globalizationInfoCache.clear();
        }
        if ((translationProperties = StringUtils.isEmpty(language) ?
                this.languagePackHandler.getTranslationProperties() : this.languagePackHandler.getTranslationProperties(language)) != null && translationProperties.size() >= 1) {
            globalizationInfoCache = this.globalizationConfigBuilder.getCache().getExpireTime() == -1L ?
                    new CaffeineCache("globalizationInfoCache", Caffeine.newBuilder().maximumSize(100000L).expireAfterWrite(Integer.MAX_VALUE, TimeUnit.DAYS).build()) :
                    new CaffeineCache("globalizationInfoCache", Caffeine.newBuilder().maximumSize(100000L).expireAfterWrite(this.globalizationConfigBuilder.getCache().getExpireTime(), TimeUnit.MINUTES).build());
            for (Map.Entry<Object, Object> entry : translationProperties.entrySet()) {
                globalizationInfoCache.put(entry.getKey(), entry.getValue());
            }
            this.globalizationInfoCacheMap.put(language, globalizationInfoCache);
            log.info("globalization Info Cache load success!");
            return true;
        }
        log.error("globalization Info Cache load fail!");
        return false;
    }
}

