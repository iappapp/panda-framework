/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.http.HttpMethod
 *  org.springframework.util.MultiValueMap
 */
package com.github.iappapp.panda.common.http;

import java.lang.reflect.Type;

import com.github.iappapp.panda.common.http.model.RestResponseDTO;
import org.springframework.http.HttpMethod;
import org.springframework.util.MultiValueMap;

public interface CommonRestHelper {
    /**
     *
     * @param method
     * @param url
     * @param param
     * @param baseHeader
     * @param type
     * @return
     * @param <T>
     */
    <T> RestResponseDTO<T> doRest(HttpMethod method, String url, Object param,
                                  MultiValueMap<String, String> baseHeader, Type type);
}

