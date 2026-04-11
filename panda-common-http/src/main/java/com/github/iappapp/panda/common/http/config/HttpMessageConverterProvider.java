/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.http.converter.HttpMessageConverter
 */
package com.github.iappapp.panda.common.http.config;

import java.util.List;
import org.springframework.http.converter.HttpMessageConverter;

public interface HttpMessageConverterProvider {
    List<HttpMessageConverter<?>> listMsgConverters();
}

