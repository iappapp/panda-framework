/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.Filter
 *  org.springframework.boot.web.servlet.FilterRegistrationBean
 *  org.springframework.boot.web.servlet.ServletComponentScan
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Conditional
 *  org.springframework.context.annotation.Configuration
 */
package com.github.iappapp.panda.common.defense.xss;

import java.util.Collections;
import javax.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@Conditional(value={XssCondition.class})
@ServletComponentScan(value={"com.dahua.panda"})
public class XssFilterRegistrationBean {
    @Bean
    public FilterRegistrationBean<XssFilter> xssFilterRegistrationBean(XssFilter xssFilter) {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setOrder(10);
        registrationBean.setFilter((Filter)xssFilter);
        registrationBean.setName("xssFilter");
        registrationBean.setUrlPatterns(Collections.singletonList("/*"));
        return registrationBean;
    }

    @Bean
    public XssFilter xssFilter() {
        return new XssFilter();
    }
}

