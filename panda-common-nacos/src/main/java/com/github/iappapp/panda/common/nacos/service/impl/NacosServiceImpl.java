/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Service
 */
package com.github.iappapp.panda.common.nacos.service.impl;

import com.github.iappapp.panda.common.nacos.bean.ServiceDTO;
import com.github.iappapp.panda.common.nacos.operation.NacosNamingOperation;
import com.github.iappapp.panda.common.nacos.service.NacosService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value="pandaNacosService")
public class NacosServiceImpl implements NacosService {

    @Autowired(required=false)
    private NacosNamingOperation nacosNamingOperation;

    @Override
    public void checkServiceExist(List<ServiceDTO> serviceDTOList) {
        this.nacosNamingOperation.checkServiceExist(serviceDTOList);
    }
}

