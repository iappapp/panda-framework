package com.github.iappapp.panda.business.resource;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;

@Slf4j
public class AuthInterfaceReportService implements SmartInitializingSingleton, ApplicationContextAware {
    @Autowired
    private RestTemplate resourceReportTemplate;
    @Autowired
    private ServerProperties serverProperties;

    private ApplicationContext applicationContext;

    @Value("${gateway.url:http://192.168.3.233:38255}")
    private String gatewayUrl;

    @Override
    public void afterSingletonsInstantiated() {
        Map<String, Object> controllerMap =
                applicationContext.getBeansWithAnnotation(Controller.class);

        List<RequestInfo> requestInfoList = Lists.newLinkedList();
        for (Map.Entry<String, Object> entry : controllerMap.entrySet()) {
            Class<?> oriClass = entry.getValue().getClass();

            String className = "";
            if (oriClass.getName().contains("$$")) {
                className = oriClass.getName().split("\\$\\$")[0];
                while (!oriClass.getName().equals(className)) {
                    oriClass = oriClass.getSuperclass();
                }
            }

            if ((oriClass.isAnnotationPresent(Controller.class)
                    && oriClass.isAnnotationPresent(ResponseBody.class))
                    || oriClass.isAnnotationPresent(RestController.class)) {
                parseBeanWithRestControllerAnnotation(oriClass, requestInfoList);
                continue;
            }
            if (oriClass.isAnnotationPresent(Controller.class)
                    && !oriClass.isAnnotationPresent(ResponseBody.class)) {
                parseBeanWithControllerAnnotation(oriClass, requestInfoList);
            }
        }

        for (RequestInfo info : requestInfoList) {
            log.info("info={}", info);
        }

        InterfaceBlackList interfaceBlackList = InterfaceBlackList.builder()
                .interfaces(requestInfoList)
                .contextPath(serverProperties.getServlet().getContextPath())
                .build();
        Executors.newFixedThreadPool(1).submit(() -> {
            int times = 0;
            while (!reportAuthInterface(interfaceBlackList) && times < 3) {
                try {
                    Thread.sleep(5000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                times++;
            }
        });
        // reportAuthInterface(interfaceBlackList);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private void parseBeanWithRestControllerAnnotation(Class<?> clazz,
                                                             List<RequestInfo> requestInfoList) {
        Class<?> oriClass = clazz;

        String className = "";
        if (oriClass.getName().contains("$$")) {
            className = oriClass.getName().split("\\$\\$")[0];
            while (!oriClass.getName().equals(className)) {
                oriClass = oriClass.getSuperclass();
            }
        }

        RequestMapping requestMapping = oriClass.getAnnotation(RequestMapping.class);
        String[] pathPrefix = new String[0];
        if (!Objects.isNull(requestMapping)) {
            pathPrefix = requestMapping.value();
        }

        Method[] methods = oriClass.getDeclaredMethods();
        List<RequestInfo> requestInfos = Lists.newArrayList();
        for (Method method : methods) {
            addRequestInfo(method, requestInfos);
        }

        for (String parentPath : pathPrefix) {
            if (StringUtils.isAllBlank(parentPath)) {
                continue;
            }
            if ("/".equals(parentPath)) {
                continue;
            }
            for (RequestInfo requestInfo : requestInfos) {
                String url = requestInfo.getUrl();
                url = !url.startsWith("/") ? "/" + url : url;
                requestInfoList.add(new RequestInfo(parentPath + url, requestInfo.getMethod()));
            }
        }
    }

    private void parseBeanWithControllerAnnotation(Class<?> clazz, List<RequestInfo> requestInfoList) {
        Class<?> oriClass = clazz;

        String className = "";
        if (oriClass.getName().contains("$$")) {
            className = oriClass.getName().split("\\$\\$")[0];
            while (!oriClass.getName().equals(className)) {
                oriClass = oriClass.getSuperclass();
            }
        }
        RequestMapping requestMapping = oriClass.getAnnotation(RequestMapping.class);
        String[] pathPrefix = new String[0];
        if (!Objects.isNull(requestMapping)) {
            pathPrefix = requestMapping.value();
        }


        Method[] methods = oriClass.getDeclaredMethods();
        List<RequestInfo> requestInfos = Lists.newArrayList();
        for (Method method : methods) {
            if (!method.isAnnotationPresent(ResponseBody.class)) {
                continue;
            }
            addRequestInfo(method, requestInfos);
        }

        for (String parentPath : pathPrefix) {
            if (StringUtils.isAllBlank(parentPath)) {
                continue;
            }
            if ("/".equals(parentPath)) {
                continue;
            }
            for (RequestInfo requestInfo : requestInfos) {
                String url = requestInfo.getUrl();
                url = !url.startsWith("/") ? "/" + url : url;
                requestInfoList.add(new RequestInfo(parentPath + url, requestInfo.getMethod()));
            }
        }
    }

    public boolean reportAuthInterface(InterfaceBlackList interfaceBlackList) {
        boolean report = false;
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.put("X-Subject-Token", Lists.newArrayList("12345678"));
            String result = resourceReportTemplate.postForObject(gatewayUrl + "/gateway/report/authInterface",
                    new HttpEntity<>(interfaceBlackList, httpHeaders), String.class, (Object) null);

            log.info("report url={} result={}", result, result);
            report = true;
        } catch (Exception ex) {
            log.error("resource report error", ex);
        }
        return report;
    }

    private void addRequestInfo(Method method, List<RequestInfo> requestInfos) {
        if (method.isAnnotationPresent(RequestMapping.class)) {
            String[] value = method.getAnnotation(RequestMapping.class).value();
            String path = value.length == 0 ? "" : value[0];
            requestInfos.add(new RequestInfo(path, RequestMethod.valueOf("GET")));
            requestInfos.add(new RequestInfo(path, RequestMethod.valueOf("POST")));
        }
        if (method.isAnnotationPresent(GetMapping.class)) {
            String[] value =  method.getAnnotation(GetMapping.class).value();
            String path = value.length == 0 ? "" : value[0];
            requestInfos.add(new RequestInfo(path, RequestMethod.valueOf("GET")));
        }
        if (method.isAnnotationPresent(PostMapping.class)) {
            String[] value = method.getAnnotation(PostMapping.class).value();
            String path = value.length == 0 ? "" : value[0];
            requestInfos.add(new RequestInfo(path, RequestMethod.valueOf("POST")));
        }
        if (method.isAnnotationPresent(PutMapping.class)) {
            String[] value = method.getAnnotation(PutMapping.class).value();
            String path = value.length == 0 ? "" : value[0];
            requestInfos.add(new RequestInfo(path, RequestMethod.valueOf("PUT")));
        }
        if (method.isAnnotationPresent(DeleteMapping.class)) {
            String[] value = method.getAnnotation(DeleteMapping.class).value();
            String path = value.length == 0 ? "" : value[0];
            requestInfos.add(new RequestInfo(path, RequestMethod.valueOf("DELETE")));
        }
    }
}
