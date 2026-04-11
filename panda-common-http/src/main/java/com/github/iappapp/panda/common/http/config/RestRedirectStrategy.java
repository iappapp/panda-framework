/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.HttpEntity
 *  org.apache.http.HttpEntityEnclosingRequest
 *  org.apache.http.HttpRequest
 *  org.apache.http.HttpResponse
 *  org.apache.http.ProtocolException
 *  org.apache.http.annotation.Contract
 *  org.apache.http.annotation.ThreadingBehavior
 *  org.apache.http.client.methods.HttpDelete
 *  org.apache.http.client.methods.HttpGet
 *  org.apache.http.client.methods.HttpPost
 *  org.apache.http.client.methods.HttpPut
 *  org.apache.http.client.methods.HttpUriRequest
 *  org.apache.http.client.methods.RequestBuilder
 *  org.apache.http.impl.client.DefaultRedirectStrategy
 *  org.apache.http.protocol.HttpContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.github.iappapp.panda.common.http.config;

import java.net.URI;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolException;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;

@Contract(threading=ThreadingBehavior.IMMUTABLE)
public class RestRedirectStrategy extends DefaultRedirectStrategy {
    private static final Logger log = LoggerFactory.getLogger(RestRedirectStrategy.class);
    private static final String[] REDIRECT_METHODS = new String[] {
            HttpMethod.GET.name(),
            HttpMethod.PUT.name(),
            HttpMethod.DELETE.name(),
            HttpMethod.POST.name()
    };

    protected boolean isRedirectable(String method) {
        for (String m : REDIRECT_METHODS) {
            if (!m.equalsIgnoreCase(method)) {
                continue;
            }
            return true;
        }
        return false;
    }

    public HttpUriRequest getRedirect(HttpRequest request, HttpResponse response, HttpContext context)
            throws ProtocolException {
        URI uri = this.getLocationURI(request, response, context);
        log.info("redirect to " + uri.toString());
        String method = request.getRequestLine().getMethod();
        if (method.equalsIgnoreCase(HttpMethod.GET.name())) {
            return new HttpGet(uri);
        }
        if (method.equalsIgnoreCase(HttpMethod.POST.name())) {
            HttpPost post = new HttpPost(uri);
            if (request instanceof HttpEntityEnclosingRequest) {
                HttpEntity oldEntity = ((HttpEntityEnclosingRequest)request).getEntity();
                post.setEntity(oldEntity);
            }
            return post;
        }
        if (method.equalsIgnoreCase(HttpMethod.PUT.name())) {
            HttpPut put = new HttpPut(uri);
            if (request instanceof HttpEntityEnclosingRequest) {
                HttpEntity oldEntity = ((HttpEntityEnclosingRequest)request).getEntity();
                put.setEntity(oldEntity);
            }
            return put;
        }
        if (method.equalsIgnoreCase(HttpMethod.DELETE.name())) {
            return new HttpDelete(uri);
        }
        int status = response.getStatusLine().getStatusCode();
        if (status == HttpStatus.SC_TEMPORARY_REDIRECT) {
            return RequestBuilder.copy(request).setUri(uri).build();
        }
        return new HttpGet(uri);
    }
}

