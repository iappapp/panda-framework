package com.github.iappapp.panda.business.globaliztion.config;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author iappapp
 * @date 2025/6/25
 * @description TODO
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(value = PandaGlobalizationAutoConfiguration.class)
public @interface EnablePandaGlobalization {

}
