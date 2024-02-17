package com.peanut.reflect.model;

import jakarta.persistence.Id;

import java.util.List;

public class MethodInfo {
    private List<ParameterInfo> parameters;
    @Id
    private String name;
    private int modifiers;
    private TypeInfo returnType;
    private List<AnnotationInfo> annotations;

    public List<ParameterInfo> getParameters() {
        return parameters;
    }

    public void setParameters(List<ParameterInfo> parameters) {
        this.parameters = parameters;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TypeInfo getReturnType() {
        return returnType;
    }

    public void setReturnType(TypeInfo returnType) {
        this.returnType = returnType;
    }

    public List<AnnotationInfo> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<AnnotationInfo> annotations) {
        this.annotations = annotations;
    }
    public int getModifiers() {
        return modifiers;
    }

    public void setModifiers(int modifiers) {
        this.modifiers = modifiers;
    }

}
