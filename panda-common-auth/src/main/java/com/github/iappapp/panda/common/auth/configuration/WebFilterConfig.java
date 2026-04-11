package com.github.iappapp.panda.common.auth.configuration;

import com.github.iappapp.panda.common.auth.filter.UserTokenFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AuthTokenProperties.class)
@ConditionalOnProperty(prefix = "panda.auth", name = "token-type", matchIfMissing = false)
public class WebFilterConfig {
    @Bean
    public FilterRegistrationBean<UserTokenFilter> userTokenFilter(AuthTokenProperties authTokenProperties) {
        String[] urlPatterns = authTokenProperties.getUrlPatterns().split(",");
        FilterRegistrationBean<UserTokenFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new UserTokenFilter(authTokenProperties));
        registrationBean.addUrlPatterns(urlPatterns);
        registrationBean.setName("UserTokenFilter");

        return registrationBean;
    }
 }
