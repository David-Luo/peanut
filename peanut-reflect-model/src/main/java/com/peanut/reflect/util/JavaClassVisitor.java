package com.peanut.reflect.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
@SuppressWarnings({ "rawtypes" })
public interface JavaClassVisitor {
    ClassInfoBuilder builder = new ClassInfoBuilder();

    default void visitDeclare(Class clazz){}

    default void visitDeclaredConstructor(Constructor constructor){}

    default void visitSuperClass(Class clazz){}

    default void visitGenericInterfaces(Type clazz){}

    default void visitField(Field field){}

    default void visitMethod(Method method){}

    default void visitAnnotation(Annotation annotation){}

}
