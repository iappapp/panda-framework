package com.github.iappapp.panda.business.resource.configuration;

import com.github.iappapp.panda.business.resource.AuthInterfaceReportService;
import com.github.iappapp.panda.business.resource.ResourceReportService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration(proxyBeanMethods = false)
public class ResourceReportConfiguration {

    @Bean
    public ResourceReportService reportService() {
        ResourceReportService reportService = new ResourceReportService();
        return reportService;
    }

    @Bean
    public RestTemplate resourceReportTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate;
    }

    @Bean
    public AuthInterfaceReportService authInterfaceReportService() {
        AuthInterfaceReportService reportService = new AuthInterfaceReportService();
        return reportService;
    }
}
