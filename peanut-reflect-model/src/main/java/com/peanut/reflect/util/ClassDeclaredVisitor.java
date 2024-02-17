package com.peanut.reflect.util;

import com.peanut.reflect.model.ClassInfo;
import com.peanut.reflect.model.FieldInfo;
import com.peanut.reflect.model.MethodInfo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

@SuppressWarnings({ "rawtypes" })
public class ClassDeclaredVisitor implements JavaClassVisitor {
    private ClassInfo classInfo;

    public ClassDeclaredVisitor(){
        classInfo = new ClassInfo();
    }

    @Override
    public void visitDeclare(Class clazz) {
        builder.buildClassSymbol(clazz,classInfo);

    }

    @Override
    public void visitDeclaredConstructor(Constructor constructor) {

    }

    @Override
    public void visitSuperClass(Class clazz) {
        classInfo.setSuperClass(builder.buildTypeInfo(clazz));
    }

    @Override
    public void visitGenericInterfaces(Type type) {
        classInfo.addSuperInterface(builder.buildTypeInfo(type));
    }

    @Override
    public void visitField(Field field) {
        FieldInfo variable = builder.buildFieldInfo(field);
        classInfo.addField(variable);
    }

    @Override
    public void visitMethod(Method method) {
        MethodInfo methodInfo = builder.buildMethod(method);
        classInfo.addMethod(methodInfo);
    }

    @Override
    public void visitAnnotation(Annotation annotation) {
        classInfo.addAnnotation(builder.buildAnnotation(annotation));
    }

    public ClassInfo getClassInfo(){
        return classInfo;
    }
}
