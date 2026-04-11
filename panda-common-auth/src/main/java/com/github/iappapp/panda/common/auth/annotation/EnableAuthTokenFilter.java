package com.github.iappapp.panda.common.auth.annotation;

import com.github.iappapp.panda.common.auth.configuration.SsoConfiguration;
import com.github.iappapp.panda.common.auth.configuration.WebFilterConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Import({
        WebFilterConfig.class,
        SsoConfiguration.class
})
@Configuration
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface EnableAuthTokenFilter {
}
