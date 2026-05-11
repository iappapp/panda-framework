package com.github.iappapp.panda.common.apiversion.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiVersionAutoConfiguration {

    @Bean
    public ApiVersionProperties apiVersionProperties() {
        return new ApiVersionProperties();
    }

    @Bean
    @ConditionalOnProperty(name = "api.version.type", havingValue = "HEADER", matchIfMissing = false)
    public ApiVersionWebMvcRegistrations headerApiVersionWebMvcRegistrations(ApiVersionProperties apiVersionProperties) {
        return new ApiVersionWebMvcRegistrations(apiVersionProperties);
    }

    @Bean
    @ConditionalOnProperty(name = "api.version.type", havingValue = "URI")
    public UriApiVersionWebMvcRegistrations uriApiVersionWebMvcRegistrations(ApiVersionProperties apiVersionProperties) {
        return new UriApiVersionWebMvcRegistrations(apiVersionProperties);
    }
}
