package com.peanut.reflect.model;

import jakarta.persistence.Id;

import java.util.List;

public class FieldInfo{
    @Id
    private String name;
    private TypeInfo type;
    private int modifiers;
    private String initializer;
    private String comment;
    private List<AnnotationInfo> annotations;
    public FieldInfo(){}
    public FieldInfo(TypeInfo type, String name, String initializer) {
        this.type=type;
        this.name = name;
        this.initializer = initializer;
    }


    public FieldInfo(TypeInfo type, String name) {
        this(type, name,null);
    }

    public String getName() {
        return name;
    }

    public TypeInfo getType() {
        return type;
    }

    public void setModifiers(int modifiers) {
        this.modifiers = modifiers;
    }
    public int getModifiers() {
        return modifiers;
    }

    public String getComment() {
        return comment;
    }

    public List<AnnotationInfo> getAnnotations() {
        return annotations;
    }

    public String getInitializer() {
        return initializer;
    }

    public void setAnnotations(List<AnnotationInfo> annotations) {
        this.annotations =annotations;
    }
}
