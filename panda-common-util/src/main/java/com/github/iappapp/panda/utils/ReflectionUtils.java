/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.annotation.AnnotationUtil
 *  cn.hutool.core.util.ArrayUtil
 *  cn.hutool.core.util.ReflectUtil
 *  cn.hutool.core.util.StrUtil
 *  org.hibernate.validator.internal.util.ReflectionHelper
 *  org.hibernate.validator.internal.util.TypeHelper
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
 */
package com.github.iappapp.panda.utils;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.internal.util.ReflectionHelper;
import org.hibernate.validator.internal.util.TypeHelper;

public class ReflectionUtils
extends ReflectUtil {
    private static final String GETTER_PREFIX = "get";
    private static final String SETTER_PREFIX = "set";
    private static final String CGLIB_CLASS_SEPARATOR = "$$";
    private static final String POINT = ".";

    public static String getFieldValue(Object obj, Field field) {
        if (Objects.isNull((Object) obj) || Objects.isNull((Object)field)) {
            return "";
        }
        try {
            Object value = ReflectUtil.getFieldValue((Object)obj, (Field)field);
            return value == null ? "" : value.toString();
        }
        catch (Exception e) {
            Object[] classFieldList = ReflectionUtils.getClassField(obj);
            if (ArrayUtil.isEmpty((Object[])classFieldList)) {
                return "";
            }
            for (Object classField : classFieldList) {
                Object o = ReflectUtil.getFieldValue((Object)obj, (Field)classField);
                String value = ReflectionUtils.getFieldValue(o, field);
                if (StringUtils.isEmpty(value))
                    continue;
                return value;
            }
            return "";
        }
    }

    private static Field[] getClassField(Object targetObject) {
        Field[] declaredFields;
        ArrayList<Field> classFieldList = new ArrayList<Field>();
        for (Field field : declaredFields = targetObject.getClass().getDeclaredFields()) {
            if (ReflectionUtils.isPrimitive(field.getType()) || ReflectionHelper.isCollection((Type)field.getGenericType()) || TypeHelper.isArray((Type)field.getGenericType())) continue;
            classFieldList.add(field);
        }
        return classFieldList.toArray(new Field[0]);
    }

    private static boolean isPrimitive(Class<?> clazz) {
        try {
            if (clazz.getName().equals(String.class.getName()) || clazz.getName().equals(Date.class.getName())) {
                return true;
            }
            return ((Class)clazz.getField("TYPE").get(null)).isPrimitive();
        }
        catch (Exception e) {
            return clazz.isPrimitive();
        }
    }

    public static Object invokeGetter(Object obj, String fieldName) {
        if (StrUtil.isBlank((CharSequence)fieldName)) {
            return null;
        }
        Object object = obj;
        for (String name : StrUtil.split((CharSequence)fieldName, (CharSequence)POINT)) {
            String getterMethodName = GETTER_PREFIX + StrUtil.upperFirst((CharSequence)name);
            object = ReflectUtil.invoke((Object)object, (String)getterMethodName, (Object[])new Object[0]);
        }
        return object;
    }

    public static void invokeSetter(Object obj, String fieldName, Object value) {
        Object object = obj;
        String[] names = StrUtil.split((CharSequence)fieldName, (CharSequence)POINT);
        for (int i = 0; i < names.length; ++i) {
            if (i < names.length - 1) {
                String getterMethodName = GETTER_PREFIX + StrUtil.upperFirst((CharSequence)names[i]);
                object = ReflectUtil.invoke((Object)object, (String)getterMethodName, (Object[])new Object[]{new Class[0], new Object[0]});
                continue;
            }
            String setterMethodName = SETTER_PREFIX + StrUtil.upperFirst((CharSequence)names[i]);
            ReflectUtil.invoke((Object)object, (String)setterMethodName, (Object[])new Object[]{value});
        }
    }

    public static void copyProperty(Object src, String srcField, Object dest, String destField) {
        ReflectionUtils.invokeSetter(dest, destField, ReflectionUtils.invokeGetter(src, srcField));
    }

    public static Object getAnnotationValue(Field[] fields, String fieldName, Class<? extends Annotation> annotationClass, String annotationAttribute) {
        Object value = null;
        for (Field field : fields) {
            if (!field.getName().equals(fieldName)) continue;
            return AnnotationUtil.getAnnotationValue((AnnotatedElement)field, annotationClass, (String)annotationAttribute);
        }
        return value;
    }

    public static String[] getFieldNames(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        LinkedList<String> fieldList = new LinkedList<String>();
        if (fields.length > 0) {
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers())) continue;
                fieldList.add(field.getName());
            }
            return fieldList.toArray(new String[fieldList.size()]);
        }
        return null;
    }

    public static Class getClassGenericType(Class clazz) {
        Type genType = clazz.getGenericSuperclass();
        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }
        Type[] params = ((ParameterizedType)genType).getActualTypeArguments();
        if (params.length == 0) {
            return Object.class;
        }
        if (!(params[0] instanceof Class)) {
            return Object.class;
        }
        return (Class)params[0];
    }

    public static Class<?> getRealClass(Object obj) {
        Class<?> superClass;
        Class<?> clazz = obj.getClass();
        if (clazz != null && clazz.getName().contains(CGLIB_CLASS_SEPARATOR) && (superClass = clazz.getSuperclass()) != null && !Object.class.equals(superClass)) {
            return superClass;
        }
        return clazz;
    }
}

