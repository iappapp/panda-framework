package com.github.iappapp.panda.common.apiversion.annotation;


import com.github.iappapp.panda.common.apiversion.config.ApiVersionAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(ApiVersionAutoConfiguration.class)
@Configuration
public @interface EnableApiVersion {
}
