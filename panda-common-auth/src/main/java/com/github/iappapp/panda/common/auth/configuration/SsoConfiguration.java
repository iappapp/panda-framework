package com.github.iappapp.panda.common.auth.configuration;

import com.github.iappapp.panda.common.auth.util.SsonCookieHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SsoConfiguration {
    @Bean
    public SsonCookieHelper ssonCookieHelper() {
        return new SsonCookieHelper();
    }
}
