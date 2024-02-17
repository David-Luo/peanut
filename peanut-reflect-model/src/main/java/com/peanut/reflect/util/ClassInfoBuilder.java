package com.peanut.reflect.util;

import com.peanut.reflect.model.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
@SuppressWarnings({ "rawtypes"})
public class ClassInfoBuilder {
    public void buildClassSymbol(Class<?> clazz,ClassInfo classInfo){
        classInfo.resetName(clazz.getPackage().getName(), clazz.getSimpleName());
        classInfo.setIsInterface(clazz.isInterface());
        classInfo.setModifiers(clazz.getModifiers());
        TypeVariable<? extends Class<?>>[] typeParameters = clazz.getTypeParameters();
        for (int i = 0; i < typeParameters.length; i++) {
            classInfo.addGenericType(buildTypeInfo(typeParameters[i]));
        }
    }
    public AnnotationInfo buildAnnotation(Annotation annotation) {
        AnnotationInfo info = new AnnotationInfo(annotation.annotationType().getName());
        Map<String, Object> values = new HashMap<>();

        for (Method method : annotation.annotationType().getDeclaredMethods()) {
            try {
                values.put(method.getName(), method.invoke(annotation, (Object[]) null));
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        info.setMembers(values);
        return info;
    }

    public MethodInfo buildMethod(Method method){
        MethodInfo info = new MethodInfo();
        info.setModifiers(method.getModifiers());
        info.setName(method.getName());
        info.setReturnType(buildTypeInfo(method.getGenericReturnType()));
        List<ParameterInfo> parameters = Arrays.stream(method.getParameters()).map(this::buildParameter).collect(Collectors.toList());
        info.setParameters(parameters);
        info.setAnnotations(visitAnnotationElement(method));
        return info;
    }

    public ParameterInfo buildParameter(Parameter parameter){
        TypeInfo type = buildTypeInfo(parameter.getParameterizedType());
        ParameterInfo variable = new ParameterInfo(type, parameter.getName());
        variable.setAnnotations(visitAnnotationElement(parameter));
        return variable;
    }

    public FieldInfo buildFieldInfo(Field field) {
        TypeInfo type = buildTypeInfo(field.getGenericType());
        FieldInfo fieldInfo = new FieldInfo(type, field.getName());
        fieldInfo.setAnnotations(visitAnnotationElement(field));
        //TODO add something else
        return fieldInfo;
    }

    public TypeInfo buildParameterizedType(ParameterizedType parameterizedType) {
        TypeInfo typeInfo = new TypeInfo(parameterizedType.getRawType().getTypeName());
        List<TypeInfo> genericTypes = Stream
                .of(parameterizedType.getActualTypeArguments())
                .map(this::buildTypeInfo)
                .collect(Collectors.toList());
        typeInfo.setGenericTypes(genericTypes);
        return typeInfo;
    }

    public TypeInfo buildTypeInfo(Type type) {
        if (type instanceof GenericArrayType) {
            return buildGenericArrayType((GenericArrayType) type);
        } else if (type instanceof WildcardType) {
            return buildWildcardType((WildcardType) type);
        } else if (type instanceof TypeVariable) {
            return buildTypeVariable((TypeVariable) type);
        } else if (type instanceof ParameterizedType) {
            return buildParameterizedType((ParameterizedType) type);
        }
        if (((Class) type).isEnum()) {
            return new TypeInfo(Enum.class.getTypeName());
        }
        return new TypeInfo(type.getTypeName());
    }

    public TypeInfo buildGenericArrayType(GenericArrayType type) {
        TypeInfo typeInfo = new TypeInfo(type.getTypeName());
        typeInfo.addGenericType(buildTypeInfo(type.getGenericComponentType()));
        typeInfo.setArray(true);
        return typeInfo;
    }

    public TypeInfo buildWildcardType(WildcardType variable) {
        TypeInfo typeInfo = new TypeInfo(variable.getTypeName());

        return typeInfo;
    }

    public TypeInfo buildTypeVariable(TypeVariable variable) {
        TypeInfo typeInfo = new TypeInfo(variable.getTypeName());
        return typeInfo;
    }

    private List<AnnotationInfo> visitAnnotationElement(AnnotatedElement element){
        Annotation[] annotations = element.getAnnotations();
        if(annotations==null||annotations.length==0){
            return null;
        }
        return Arrays.stream(annotations)
                .map(this::buildAnnotation)
                .collect(Collectors.toList());
    }
}
