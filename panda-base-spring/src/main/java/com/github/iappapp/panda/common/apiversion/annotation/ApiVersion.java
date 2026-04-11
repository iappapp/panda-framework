package com.github.iappapp.panda.common.apiversion.annotation;

import com.github.iappapp.panda.common.apiversion.constant.ApiConstant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiVersion {
    /**
     *
     * @return
     */
    String value() default ApiConstant.DEFAULT_VERSION;
}
