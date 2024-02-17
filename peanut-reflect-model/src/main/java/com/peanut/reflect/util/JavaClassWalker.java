package com.peanut.reflect.util;


import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
@SuppressWarnings({ "rawtypes" })
public class JavaClassWalker {

    public void walk(Class clazz, JavaClassVisitor visitor) {
        assert clazz != null;
        
        visitor.visitDeclare(clazz);
        Class superclass = clazz.getSuperclass();
        if(superclass !=null) {
            visitor.visitSuperClass(superclass);
        }

        Annotation[] annotations = clazz.getAnnotations();
        if (annotations != null && annotations.length>0){
            Arrays.stream(annotations).forEach(visitor::visitAnnotation);
        }

        Type[] genericInterfaces = clazz.getGenericInterfaces();
        if(genericInterfaces !=null && genericInterfaces.length>0){
            Arrays.stream(genericInterfaces).forEach(visitor::visitGenericInterfaces);
        }

        Constructor[] constructors = clazz.getDeclaredConstructors();
        if(constructors !=null && constructors.length>0){
            Arrays.stream(constructors).forEach(visitor::visitDeclaredConstructor);
        }

        Field[] fields = clazz.getDeclaredFields();
        if(fields != null && fields.length>0){
            Arrays.stream(fields).forEach(visitor::visitField);
        }
        visitSuperClassFields(clazz, visitor);


        Method[] methods = clazz.getMethods();
        if(methods != null && methods.length>0){
            Arrays.stream(methods).forEach(visitor::visitMethod);
        }

    }

    private void visitSuperClassFields(Class clazz, JavaClassVisitor visitor){
        Class superClass = clazz.getSuperclass();
        if(superClass == null){
            return;
        }
        Field[] fields = superClass.getDeclaredFields();
        if(fields != null && fields.length>0){
            Arrays.stream(fields).forEach(visitor::visitField);
        }

        visitSuperClassFields(superClass,visitor);
    }
}
