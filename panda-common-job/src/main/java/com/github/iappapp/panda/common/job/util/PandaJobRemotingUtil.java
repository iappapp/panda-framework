package com.github.iappapp.panda.common.job.util;

import com.github.iappapp.panda.common.job.biz.model.ReturnT;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public class PandaJobRemotingUtil {
    private static Logger logger = LoggerFactory.getLogger(PandaJobRemotingUtil.class);

    public static final String PANDA_JOB_ACCESS_TOKEN = "PANDA-JOB-ACCESS-TOKEN";

    private static void trustAllHosts(HttpsURLConnection connection) {
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            SSLSocketFactory newFactory = sc.getSocketFactory();
            connection.setSSLSocketFactory(newFactory);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        connection.setHostnameVerifier(new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
    }

    private static final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }

        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }
    }};

    public static ReturnT postBody(String url, String accessToken, int timeout, Object requestObj, Class returnTargClassOfT) {
        HttpURLConnection connection = null;
        BufferedReader bufferedReader = null;
        try {
            URL realUrl = new URL(url);
            connection = (HttpURLConnection) realUrl.openConnection();
            boolean useHttps = url.startsWith("https");
            if (useHttps) {
                HttpsURLConnection https = (HttpsURLConnection) connection;
                trustAllHosts(https);
            }
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setReadTimeout(timeout * 1000);
            connection.setConnectTimeout(5000);
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            connection.setRequestProperty("Accept-Charset", "application/json;charset=UTF-8");
            if (accessToken != null && accessToken.trim().length() > 0)
                connection.setRequestProperty("PANDA-JOB-ACCESS-TOKEN", accessToken);
            connection.connect();
            if (requestObj != null) {
                String requestBody = GsonTool.toJson(requestObj);
                DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                dataOutputStream.write(requestBody.getBytes("UTF-8"));
                dataOutputStream.flush();
                dataOutputStream.close();
            }
            int statusCode = connection.getResponseCode();
            if (statusCode != 200)
                return new ReturnT(500, "panda-rpc remoting fail, StatusCode(" + statusCode + ") invalid. for url : " + url);
            bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null)
                result.append(line);
            String resultJson = result.toString();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ReturnT(500, "panda-rpc remoting error(" + e.getMessage() + "), for url : " + url);
        } finally {
            try {
                if (bufferedReader != null)
                    bufferedReader.close();
                if (connection != null)
                    connection.disconnect();
            } catch (Exception e2) {
                logger.error(e2.getMessage(), e2);
            }
        }
        return ReturnT.FAIL;
    }

    public static ReturnT post(String url, String userName, String password, int timeout, Class<?> returnTargetClass) {
        HttpURLConnection connection = null;
        BufferedReader bufferedReader = null;
        try {
            URL realUrl = new URL(url + "?userName=" + userName + "&password=" + password);
            connection = (HttpURLConnection) realUrl.openConnection();
            boolean useHttps = url.startsWith("https");
            if (useHttps) {
                HttpsURLConnection https = (HttpsURLConnection) connection;
                trustAllHosts(https);
            }
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setReadTimeout(timeout * 1000);
            connection.setConnectTimeout(timeout * 1000);
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            connection.setRequestProperty("Accept-Charset", "application/json;charset=UTF-8");
            connection.connect();
            int statusCode = connection.getResponseCode();
            if (statusCode != 200) {
                return new ReturnT(500, "panda-rpc remoting fail, StatusCode(" + statusCode + ") invalid. for url : " + url);
            }
            bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null)
                result.append(line);
            String resultJson = result.toString();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ReturnT(500, "panda-rpc remoting error(" + e.getMessage() + "), for url : " + url);
        } finally {
            try {
                if (bufferedReader != null)
                    bufferedReader.close();
                if (connection != null)
                    connection.disconnect();
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
        return ReturnT.FAIL;
    }

    public static String post(String url, String cookie, Map<String, String> paramMap, int timeout) {
        HttpURLConnection connection = null;
        BufferedReader bufferedReader = null;
        try {
            URL realUrl = new URL(url + "?" + convert(paramMap));
            connection = (HttpURLConnection) realUrl.openConnection();
            boolean useHttps = url.startsWith("https");
            if (useHttps) {
                HttpsURLConnection https = (HttpsURLConnection) connection;
                trustAllHosts(https);
            }
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setReadTimeout(timeout * 1000);
            connection.setConnectTimeout(3000);
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            connection.setRequestProperty("Accept-Charset", "application/json;charset=UTF-8");
            connection.setRequestProperty("Cookie", cookie);
            connection.connect();
            int statusCode = connection.getResponseCode();
            if (statusCode != 200)
                return null;
            bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null)
                result.append(line);
            String resultJson = result.toString();
            return resultJson;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            try {
                if (bufferedReader != null)
                    bufferedReader.close();
                if (connection != null)
                    connection.disconnect();
            } catch (Exception e2) {
                logger.error(e2.getMessage(), e2);
            }
        }
        return null;
    }

    private static String convert(Map<String, String> paramMap) {
        StringBuilder sb = new StringBuilder(128);
        try {
            for (String str : paramMap.keySet()) {
                if (StringUtils.isEmpty(paramMap.get(str)))
                    continue;
                sb.append(str).append("=").append(URLEncoder.encode(paramMap.get(str), "UTF-8")).append("&");
            }
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
        }
        return (sb.length() != 0) ? sb.substring(0, sb.length() - 1) : "";
    }
}
