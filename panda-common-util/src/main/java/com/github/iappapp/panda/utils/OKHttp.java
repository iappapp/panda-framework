package com.github.iappapp.panda.utils;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by liush2 on 2017/3/21.
 *
 */
@Slf4j
public class OKHttp {

    private static final int timeout = 5000;

    public static String post(String url, String obj) {
        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();

            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Content-Type", "application/json;charset=UTF-8");

            //设置请求和传输超时时间
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout).build();
            httpPost.setConfig(requestConfig);

            // 解决中文乱码问题
            StringEntity stringEntity = new StringEntity(obj, "UTF-8");
            stringEntity.setContentEncoding("UTF-8");

            httpPost.setEntity(stringEntity);

            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
                @Override
                public String handleResponse(final HttpResponse response)
                        throws IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {

                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        log.info("Unexpected response status:{} response={}", status, response);
                        return null;
                    }
                }
            };
            String responseBody = httpclient.execute(httpPost, responseHandler);
            log.info(logHttpMap(url, obj, responseBody));
            return responseBody;
        } catch (ClientProtocolException e1) {
            log.error("http exception:{}", e1.getMessage());
        } catch (IOException e) {
            log.error("io exception:{}", e.getMessage());
        }
        return null;
    }

    public static String logHttpMap(String url, String params, String result) {
        StringBuilder sb = new StringBuilder();
        sb.append("url=").append(url);
        sb.append(" params=").append(params);
        sb.append(" result=").append(!StringUtils.isEmpty(result) && result.length() > 2048 ? result.substring(0, 2048) : result);

        return sb.toString();
    }

    public static String doPost(String url, Map<String, Object> param) {
        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();

            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

            //设置请求和传输超时时间
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(timeout).setConnectTimeout(timeout).build();
            httpPost.setConfig(requestConfig);

            List<NameValuePair> dataList = new ArrayList<>();

            for (Map.Entry<String, Object> entry : param.entrySet()) {
                if (entry.getKey() == null || entry.getValue() == null) {
                    continue;
                }
                dataList.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())));
            }
            // 解决中文乱码问题
            HttpEntity entity = new UrlEncodedFormEntity(dataList);
            httpPost.setEntity(entity);


            ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
                @Override
                public String handleResponse(final HttpResponse response)
                        throws IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= 200 && status < 300) {

                        HttpEntity entity = response.getEntity();
                        return entity != null ? EntityUtils.toString(entity) : null;
                    } else {
                        log.info("Unexpected response status:{} response={}", status, response);
                        return null;
                    }
                }
            };
            String responseBody = httpclient.execute(httpPost, responseHandler);
            log.info(logHttpMap(url, JSON.toJSONString(param), responseBody));
            return responseBody;
        } catch (ClientProtocolException e1) {
            log.error("http exception:{}", e1.getMessage());
        } catch (IOException e) {
            log.error("io exception:{}", e.getMessage());
        }
        return null;
    }

}
