/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Resource
 *  org.springframework.web.bind.annotation.PostMapping
 *  org.springframework.web.bind.annotation.RequestBody
 *  org.springframework.web.bind.annotation.RestController
 */
package com.github.iappapp.panda.common.nacos.controller;

import com.github.iappapp.panda.common.nacos.bean.ServiceDTO;
import com.github.iappapp.panda.common.nacos.service.NacosService;
import java.util.List;
import javax.annotation.Resource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController(value="pandaNacosClientController")
@ConditionalOnProperty(prefix = "panda.nacos", name = "enable", havingValue = "true", matchIfMissing = false)
public class NacosClientController {
    @Resource(name="pandaNacosService")
    private NacosService nacosService;

    @PostMapping(value={"/nacos/checkServices"})
    public List<ServiceDTO> checkServices(@RequestBody List<ServiceDTO> serviceDTOList) {
        this.nacosService.checkServiceExist(serviceDTOList);
        return serviceDTOList;
    }
}

