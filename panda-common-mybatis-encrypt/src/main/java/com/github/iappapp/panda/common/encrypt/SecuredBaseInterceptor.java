package com.github.iappapp.panda.common.encrypt;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author iappapp
 * @date 2025-09-08
 * 添加对父类中被注解的字段的支持
 */
public abstract class SecuredBaseInterceptor {
    private final Logger LOGGER = LoggerFactory.getLogger(SecuredBaseInterceptor.class);

    public Field[] getDeclaredField(Object object) {
        List<Field> fields = new ArrayList<>();
        for (Class<?> clazz = object.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                Field[] field = clazz.getDeclaredFields();
                if (null != field && field.length > 0) {
                    fields.addAll(Arrays.asList(field));
                }
            } catch (Exception e) {
                LOGGER.error("获取bean字段列表出错", e);
            }
        }

        Field[] field = new Field[fields.size()];
        return fields.toArray(field);
    }

    protected Integer getAlgorithm(String encryptStr) {
        Integer algorithmCode = 1;
        if (encryptStr.contains("$")) {
            String[] datas = encryptStr.split("\\$");
            if (3 != datas.length) {
                LOGGER.error("encryptStr is inValid");
                return -1;
            }
            algorithmCode = Integer.valueOf(datas[1]);
        }
        return algorithmCode;
    }

    public static final class OriginValueHolder {
        private final Object target;
        private final Field field;
        private final Object value;

        public OriginValueHolder(Object target, Field field, Object value) {
            this.target = target;
            this.field = field;
            this.value = value;
        }

        public void update() throws IllegalAccessException {
            field.set(target, value);
        }
    }

    protected void restore(List<OriginValueHolder> holderList) {
        if (CollectionUtils.isEmpty(holderList)) {
            return;
        }

        for (OriginValueHolder holder : holderList) {
            try {
                holder.update();
            } catch (IllegalAccessException exception) {
                LOGGER.warn("origin value restore fail {}", exception.getMessage());
            }
        }
    }
}
