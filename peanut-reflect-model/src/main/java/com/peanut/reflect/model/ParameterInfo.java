package com.peanut.reflect.model;

import jakarta.persistence.Id;

import java.util.List;
import java.util.Objects;

public class ParameterInfo {
    @Id
    private String name;
    private TypeInfo type;
    private String comment;
    private List<AnnotationInfo> annotations;
    public ParameterInfo(){}
    @Override
    public ParameterInfo clone(){
        ParameterInfo c = new ParameterInfo(type,name);
        c.comment = comment;
        c.annotations = annotations;
        return c;
    }

    public ParameterInfo(TypeInfo type, String name){
        this.type = type;
        this.name = name;
    }
    public ParameterInfo(String type, String name){
        this.type = new TypeInfo(type);
        this.name = name;
    }

    public TypeInfo getType() {
        return type;
    }

    public void setType(TypeInfo type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<AnnotationInfo> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<AnnotationInfo> annotations) {
        this.annotations = annotations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParameterInfo variable = (ParameterInfo) o;
        return type.equals(variable.type) && name.equals(variable.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, name);
    }

    @Override
    public String toString() {
        return '\"'+name + '\"'+':'+'\"'+type+'\"';
    }
}
