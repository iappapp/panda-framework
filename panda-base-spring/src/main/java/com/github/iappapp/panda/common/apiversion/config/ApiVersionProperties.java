package com.github.iappapp.panda.common.apiversion.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

@Data
@ConfigurationProperties(prefix = "api.version")
public class ApiVersionProperties implements Serializable {
    private Type type = Type.HEADER;

    private String header = "X-API-VERSION";

    public enum Type {
        URI,
        HEADER,
        PARAM,
        ;
    }

    public enum UriLocation {
        BEGIN,
        END,
        ;
    }
}
