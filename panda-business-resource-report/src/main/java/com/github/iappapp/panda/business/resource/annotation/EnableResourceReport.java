package com.github.iappapp.panda.business.resource.annotation;

import com.github.iappapp.panda.business.resource.configuration.ResourceReportConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Import(ResourceReportConfiguration.class)
@Target({ElementType.TYPE})
@Configuration
public @interface EnableResourceReport {
}
