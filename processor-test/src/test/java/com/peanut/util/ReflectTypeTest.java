package com.peanut.util;

import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.util.List;
import java.util.Map;

public class ReflectTypeTest {
    public static void main(String[] args) {
        A<String> a = new A();
        System.out.println(a.getClass().getName());
        Method declaredMethod = a.getClass().getDeclaredMethods()[0];
        TypeVariable<Method> typeParameter = declaredMethod.getTypeParameters()[0];
        System.out.println("typeParameter.getName():"+typeParameter.getName());
        System.out.println(declaredMethod.getName());
        System.out.println(declaredMethod.getReturnType().getTypeParameters());
    }

    static class A<T>{
        <K> Map<K, List<? extends T>> getMap(K key){
            return null;
        }
    }
}
