/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.bean.BeanDesc$PropDesc
 *  cn.hutool.core.bean.BeanUtil
 *  cn.hutool.core.util.ReflectUtil
 */
package com.github.iappapp.panda.utils;

import cn.hutool.core.bean.BeanDesc;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ReflectUtil;
import java.util.ArrayList;
import java.util.Collection;

public class BeanUtils extends BeanUtil {
    public static String[] getNullPropertyNames(Object obj) {
        Collection<BeanDesc.PropDesc> props = BeanUtil.getBeanDesc(obj.getClass()).getProps();
        String[] resultArray = new String[]{};
        if (props.size() == 0) {
            return resultArray;
        }
        ArrayList<String> resultList = new ArrayList<String>();
        for (BeanDesc.PropDesc prop : props) {
            Object value = ReflectUtil.getFieldValue(obj, prop.getFieldName());
            if (value != null) {
                continue;
            }
            resultList.add(prop.getFieldName());
        }
        return resultList.toArray(resultArray);
    }
}

