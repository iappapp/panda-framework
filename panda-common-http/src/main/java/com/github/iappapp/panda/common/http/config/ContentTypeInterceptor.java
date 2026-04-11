/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.http.HttpHeaders
 *  org.springframework.http.HttpMethod
 *  org.springframework.http.HttpRequest
 *  org.springframework.http.MediaType
 *  org.springframework.http.client.ClientHttpRequestExecution
 *  org.springframework.http.client.ClientHttpRequestInterceptor
 *  org.springframework.http.client.ClientHttpResponse
 */
package com.github.iappapp.panda.common.http.config;

import java.io.IOException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class ContentTypeInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {
        HttpHeaders headers = request.getHeaders();
        if (HttpMethod.POST == request.getMethod() && null == headers.getContentType()) {
            headers.setContentType(MediaType.APPLICATION_JSON);
        }
        return execution.execute(request, body);
    }
}

