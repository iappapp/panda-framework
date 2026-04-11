/*
 * Decompiled with CFR 0.152.
 */
package com.github.iappapp.panda.common.nacos.service;

import com.github.iappapp.panda.common.nacos.bean.ServiceDTO;
import java.util.List;

public interface NacosService {
    /**
     *
     * @param serviceDTOList
     */
    void checkServiceExist(List<ServiceDTO> serviceDTOList);
}

