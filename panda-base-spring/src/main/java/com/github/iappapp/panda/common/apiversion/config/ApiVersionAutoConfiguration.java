package com.github.iappapp.panda.common.apiversion.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties(ApiVersionProperties.class)
@Configuration
public class ApiVersionAutoConfiguration {

    @Bean
    public ApiVersionWebMvcRegistrations apiVersionWebMvcRegistrations(ApiVersionProperties apiVersionProperties) {
        return new ApiVersionWebMvcRegistrations();
    }
}
