package com.github.iappapp.panda.business.globaliztion.config;

import com.github.iappapp.panda.business.globaliztion.cache.GlobalizationCache;
import com.github.iappapp.panda.business.globaliztion.config.GlobalizationConfigBuilder;
import com.github.iappapp.panda.business.globaliztion.handler.GlobalizationHandler;
import com.github.iappapp.panda.business.globaliztion.handler.LanguagePackHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author iappapp
 * @date 2025/6/25
 * @description TODO
 */
@Configuration
@EnableConfigurationProperties(value = GlobalizationConfigBuilder.class)
public class PandaGlobalizationAutoConfiguration {

    @Bean
    public GlobalizationCache globalizationCache() {
        return new GlobalizationCache();
    }

    @Bean
    public GlobalizationHandler globalizationHandler() {
        return new GlobalizationHandler();
    }

    @Bean
    public LanguagePackHandler languagePackHandler() {
        return new LanguagePackHandler();
    }
}
