package com.peanut.util;

import com.peanut.reflect.model.ClassInfo;
import com.peanut.reflect.model.FieldInfo;
import com.peanut.reflect.model.MethodInfo;

import java.lang.reflect.Modifier;

public class JavaBeanUtils {
    
    public static String getterName(String fieldName) {
        return  "get" + StringUtils.capitalize(fieldName);
    }
    public static String setterName(String fieldName) {
        return  "set" + StringUtils.capitalize(fieldName);
    }

    /**
     * 扩展JavaBean的getter方法名，对boolean类型的属性getter做特殊处理。以下均认为是合法的getter方法。
     * 属性名： isDisable
     * getter方法名：isDisable(), getIsDisable(), getDisable();<br>
     * 属性名： boolean disable
     * getter方法名：isDisable(), getDisable();<br>
     * @param fieldName
     * @return getterMethodName
     */
    public static String[] getterNameForBoolean(String fieldName) {
        if (StringUtils.isBlank(fieldName)) {
            throw new IllegalArgumentException("属性名称不能为空");
        }
        if (fieldName.startsWith("is")) {
            return new String[] { fieldName, "get" + StringUtils.capitalize(fieldName), "get" + fieldName.substring(2, fieldName.length()) };
        } else {
            return new String[] { "is" + StringUtils.capitalize(fieldName), "get" + StringUtils.capitalize(fieldName)};
        }
    }

    public static boolean isBoolean(String type){
        if (("java.lang.Boolean".equals(type)) || "boolean".equals(type)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isJavaBean(String className) {
        Class<?> clazz;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("找不到类："+className);
        }
        return JavaBeanValidator.isJavaBean(clazz);
    }

    public static boolean isGetter(MethodInfo info){
        int modifiers = info.getModifiers();;
        return Modifier.isPublic(modifiers) && !Modifier.isStatic(modifiers)
                && info.getParameters().isEmpty();
    }
    public static MethodInfo findGetter(ClassInfo targetBean, FieldInfo filed, String getter){
        return targetBean.getMethods().stream()
                .filter(m -> m.getName().equals(getter))
                .filter(info -> isGetter(info) && info.getReturnType().equals(filed.getType()))
                .findFirst().orElse(null);
    }
    public static boolean isSetter(MethodInfo info){
        int modifiers = info.getModifiers();;
        return Modifier.isPublic(modifiers)
                && !Modifier.isStatic(modifiers)
                && void.class.getName().equals(info.getReturnType().getName())
                && info.getParameters().size() == 1;
    }
    public static MethodInfo findSetter(ClassInfo targetBean, FieldInfo filed, String setter){
        return targetBean.getMethods().stream()
                .filter(m -> m.getName().equals(setter))
                .filter(JavaBeanUtils::isSetter)
                .filter(info -> info.getParameters().get(0).getType().equals(filed.getType()))
                .findFirst().orElse(null);
    }

    public static MethodInfo findSetter(ClassInfo classInfo,FieldInfo filed){
       return findSetter(classInfo,filed, setterName(filed.getName()));
    }
    public static MethodInfo findGetter(ClassInfo classInfo,FieldInfo filed){
        MethodInfo result = null;
        if(isBoolean(filed.getType().getName())){
            String[] getterNames = getterNameForBoolean(filed.getName());
            for (String getterName : getterNames) {
                result = findGetter(classInfo,filed,getterName);
                if (result != null) {
                    break;
                }
            }
        }else{
            result = findGetter(classInfo,filed, getterName(filed.getName()));
        }
//        if (result==null) {
//            throw new IllegalArgumentException(classInfo.getSimpleName() + "没有找到" + filed.getName() + "对应的getter方法");
//        }
        return result;
    }
}
