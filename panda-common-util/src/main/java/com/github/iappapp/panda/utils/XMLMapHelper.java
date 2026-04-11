/* 
 * Copyright (c) 2014-2015 21cn Inc., All Rights Reserved.
 */
package com.github.iappapp.panda.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * </p>
 *
 * @author 黄伟胜(huangweisheng)
 */
public class XMLMapHelper {

    /**
     * 帮助类无需实例化
     */
    private XMLMapHelper() {
        // 空构造函数
    }

    private final static Map<Class<?>, Object> map = new HashMap<Class<?>, Object>();

    @SuppressWarnings("unchecked")
    public static Field map(String name, Class<?> claz) {

        if (!map.containsKey(name)) {
            map.put(claz, new HashMap<String, Field>());
        }
        Map<String, Field> fieldMap = (Map<String, Field>) map.get(claz);

        if (fieldMap.containsKey(name)) {
            return fieldMap.get(name);
        }

        // String propertyName = (char) (name.charAt(0) | 0x20) +
        // name.substring(1).replace("_", "");
        char[] chars = name.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (i > 0 && chars[i] == '_' && (i + 1) < chars.length && chars[i + 1] != '_') {
                chars[i + 1] = Character.toUpperCase(chars[i + 1]);
            }
        }

        String propertyName = new String(chars).replace("_", "");

        Field result = null;
        try {
            result = claz.getDeclaredField(propertyName);
        } catch (NoSuchFieldException e) {
            // 忽略异常，由调用者判断结果是否为null并做日志
        } catch (SecurityException e) {
            // 忽略异常，由调用者判断结果是否为null并做日志
        }

        fieldMap.put(name, result);
        return result;
    }

}
