package com.github.iappapp.panda.common.http.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.Lists;

import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import org.apache.http.HeaderElement;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriTemplateHandler;

@EnableConfigurationProperties(value = {HttpClientProperties.class,
        HttpClientPoolProperties.class,
        HttpInnerNginxProperties.class})
@Configuration
public class CommonHttpClientConfiguration implements InitializingBean {
    private static final Logger log = LoggerFactory.getLogger(CommonHttpClientConfiguration.class);
    private volatile HttpConnectionMonitor httpConnectionMonitor;
    @Autowired
    private HttpClientPoolProperties httpClientPoolProperties;
    @Autowired
    private HttpClientProperties httpClientProperties;
    @Autowired(required = false)
    private ResponseErrorHandler responseErrorHandler;
    @Autowired(required = false)
    private HttpMessageConverterProvider httpMessageConverterProvider;

    @Override
    public void afterPropertiesSet() {
        this.httpConnectionMonitor = new HttpConnectionMonitor(this.httpClientPoolProperties.getIdleTimeout());
        this.httpConnectionMonitor.start();
    }

    @Bean(name = {"pandaRestTemplate"})
    public RestTemplate getRestTemplate(HttpComponentsClientHttpRequestFactory pandaClientHttpRequestFactory) {
        return this.configBaseRestTemplate(pandaClientHttpRequestFactory);
    }

    @Bean(name = {"pandaDisableRedirectRestTemplate"})
    public RestTemplate pandaDisableRedirectRestTemplate(HttpComponentsClientHttpRequestFactory
                                                                     disableRedirectHttpRequestFactory) {
        return this.configBaseRestTemplate(disableRedirectHttpRequestFactory);
    }

    @Bean(name = {"pandaClientHttpRequestFactory"})
    public HttpComponentsClientHttpRequestFactory pandaClientHttpRequestFactory() throws NoSuchAlgorithmException,
            KeyStoreException, KeyManagementException {
        HttpClientBuilder httpClientBuilder = this.getHttpClientBuilder(true,
                this.httpClientPoolProperties.getSocketTimeout(),
                this.httpClientPoolProperties.getMaxTotalConnect(),
                this.httpClientPoolProperties.getMaxConnectPerRoute());
        return new HttpComponentsClientHttpRequestFactory(httpClientBuilder.build());
    }

    @Bean(name = {"disableRedirectHttpRequestFactory"})
    public HttpComponentsClientHttpRequestFactory disableRedirectHttpRequestFactory() throws NoSuchAlgorithmException,
            KeyStoreException, KeyManagementException {
        HttpClientBuilder httpClientBuilder =
                this.getHttpClientBuilder(false,
                        this.httpClientPoolProperties.getSocketTimeout(),
                        this.httpClientPoolProperties.getDisableRedirectMaxTotalConnect(),
                        this.httpClientPoolProperties.getDisableRedirectMaxTotalConnect());
        return new HttpComponentsClientHttpRequestFactory(httpClientBuilder.build());
    }

    public RestTemplate configBaseRestTemplate(HttpComponentsClientHttpRequestFactory clientHttpRequestFactory) {
        RestTemplate template = new RestTemplate();
        if (this.httpMessageConverterProvider != null) {
            template.setMessageConverters(this.httpMessageConverterProvider.listMsgConverters());
        } else {
            List<HttpMessageConverter<?>> messageConverters = template.getMessageConverters();
            messageConverters.removeIf(converter -> converter instanceof StringHttpMessageConverter);
            messageConverters.add(new StringHttpMessageConverter(Charset.forName(this.httpClientPoolProperties.getCharset())));
            messageConverters.forEach(converter -> {
                if (converter instanceof MappingJackson2HttpMessageConverter) {
                    ((MappingJackson2HttpMessageConverter) converter).getObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
                }
            });
            template.setMessageConverters(messageConverters);
        }
        template.setErrorHandler((this.responseErrorHandler == null ? new DefaultResponseErrorHandler() : this.responseErrorHandler));
        List<ClientHttpRequestInterceptor> interceptors = Lists.newArrayList(new ContentTypeInterceptor());
        template.setInterceptors(interceptors);
        template.setRequestFactory(clientHttpRequestFactory);
        DefaultUriBuilderFactory uriFactory = new DefaultUriBuilderFactory();
        uriFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);
        template.setUriTemplateHandler(uriFactory);
        return template;
    }

    public HttpClientBuilder getHttpClientBuilder(boolean enableDirect, int socketTimeout, int maxTotalConnect, int maxConnectPerRoute) throws KeyStoreException, KeyManagementException, NoSuchAlgorithmException {
        PoolingHttpClientConnectionManager connectionManager =
                this.poolingConnectionManager(maxTotalConnect, maxConnectPerRoute, socketTimeout);
        HttpClientBuilder httpClientBuilder =
                HttpClientBuilder.create().setConnectionManager(connectionManager)
                        .setRetryHandler(new DefaultHttpRequestRetryHandler(this.httpClientPoolProperties.getRetryTimes(), true))
                        .setDefaultRequestConfig(RequestConfig.custom()
                                .setConnectTimeout(this.httpClientPoolProperties.getConnectTimeout())
                                .setSocketTimeout(socketTimeout)
                                .setConnectionRequestTimeout(this.httpClientPoolProperties.getConnectionRequestTimeout()).build())
                        .setDefaultSocketConfig(SocketConfig.custom().setTcpNoDelay(true).setSoKeepAlive(true).setSoReuseAddress(true).build())
                        .setRetryHandler(new DefaultHttpRequestRetryHandler(this.httpClientPoolProperties.getRetryTimes(), true))
                        .setUserAgent("PSI_V2.X-HttpClient_V4")
                        .setDefaultHeaders(Lists.newArrayList(new BasicHeader("Connection", "Keep-Alive")))
                        .setKeepAliveStrategy((response, context) -> {
                            BasicHeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator("Keep-Alive"));
                            while (it.hasNext()) {
                                HeaderElement he = it.nextElement();
                                String param = he.getName();
                                String value = he.getValue();
                                if (value == null || !"timeout".equalsIgnoreCase(param)) continue;
                                try {
                                    return Long.parseLong(value) * 1000L;
                                } catch (NumberFormatException numberFormatException) {
                                }
                            }
                            return socketTimeout;
                        }).setConnectionManagerShared(true);
        if (enableDirect) {
            httpClientBuilder.setRedirectStrategy(new RestRedirectStrategy());
        } else {
            httpClientBuilder.disableRedirectHandling();
        }
        return httpClientBuilder;
    }

    public PoolingHttpClientConnectionManager poolingConnectionManager(int maxTotalConnect,
                                                                       int maxConnectPerRoute,
                                                                       int socketTimeout) throws KeyStoreException,
                                                                            KeyManagementException, NoSuchAlgorithmException {
        RegistryBuilder registryBuilder = RegistryBuilder.create();
        registryBuilder = registryBuilder.register("http", PlainConnectionSocketFactory.getSocketFactory());
        if (this.httpClientProperties.getSupport().booleanValue()) {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(keyStore, (chain, authType) -> true).build();
            NoopHostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;
            SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
            registryBuilder = registryBuilder.register("https", csf);
        }
        PoolingHttpClientConnectionManager connectionManager =
                new PoolingHttpClientConnectionManager(registryBuilder.build(),
                        null,
                        null,
                        null,
                        socketTimeout,
                        TimeUnit.MILLISECONDS);
        log.info("Set http client pool, size= {} , route= {}", this.httpClientPoolProperties.getMaxTotalConnect(),
                this.httpClientPoolProperties.getMaxConnectPerRoute());
        connectionManager.setMaxTotal(maxTotalConnect);
        connectionManager.setDefaultMaxPerRoute(maxConnectPerRoute);
        this.httpConnectionMonitor.addConnectionManager(connectionManager);
        return connectionManager;
    }
}

