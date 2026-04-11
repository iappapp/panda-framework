package com.github.iappapp.panda.common.auth.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "panda.auth")
@Data
public class AuthTokenProperties {
    /**
     * HEADER
     * COOKIE
     */
    private String tokenType;

    /**
     *
     */
    private String tokenName = "jwtToken";

    /**
     *
     */
    private String secret;
    /**
     *
     */
    private List<String> ignoreUrlPatterns;

    /**
     *
     */
    private String urlPatterns;
}
