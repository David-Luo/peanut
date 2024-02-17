package com.peanut.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class JavaBeanValidator {

    public static boolean isJavaBean(Class<?> clazz) {
        // 检查类是否为public
        if (!Modifier.isPublic(clazz.getModifiers())) {
            return false;
        }
        if (clazz.isInterface()
                ||clazz.isEnum()
                ||clazz.isAnnotation()
                ||clazz.isPrimitive()
                ||clazz.isArray()
                ||clazz.isAnonymousClass()){
            return false;
        }

        // 检查是否存在无参构造函数
        try {
            Constructor<?> ctor = clazz.getConstructor();
            if (ctor == null || !Modifier.isPublic(ctor.getModifiers())) {
                return false;
            }
        } catch (NoSuchMethodException e) {
            return false;
        }
        Field[] attributes = Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> Modifier.isPrivate(field.getModifiers()))// 忽略非private字段
                .filter(field -> !Modifier.isStatic(field.getModifiers()))// 忽略static字段
                .toArray(Field[]::new);

        if (attributes.length==0){
            return false;
        }
        // 检查所有属性是否都有getter和setter
        for (Field field : attributes) {
            String propertyName = field.getName();
            String getterName = "get" + capitalize(propertyName);
//            String setterName = "set" + capitalize(propertyName);

            // 检查getter
            Method getter = findMethod(clazz, getterName, new Class<?>[]{});
            if (getter == null || !Modifier.isPublic(getter.getModifiers())) {
                // 如果是布尔型属性，尝试查找isGetter
                if (field.getType() == Boolean.class || field.getType() == boolean.class) {
                    getterName = getBooleanGetterAlias(propertyName);
                    getter = findMethod(clazz, getterName, new Class<?>[]{});
                    if (getter == null || !Modifier.isPublic(getter.getModifiers())) {
                        return false;
                    }
                } else {
                    return false;
                }
            }

            // 检查setter
//            Method setter = findMethod(clazz, setterName, new Class<?>[]{field.getType()});
//            if (setter == null || !Modifier.isPublic(setter.getModifiers())) {
//                return false;
//            }
        }

        return true;
    }
    private static String getBooleanGetterAlias(String propertyName) {
        if(propertyName.startsWith("is")){
            return propertyName;
        }else {
            return "is" + capitalize(propertyName);
        }
    }
    private static Method findMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        try {
            return clazz.getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private static String capitalize(String str) {
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
}