package com.peanut.value.gen.model;

import com.peanut.reflect.model.ClassInfo;

import java.util.List;

public class BeanSourceCodeDeclared {
    private ClassInfo bean;
    private List<AttributeDeclared> attributes;

    public ClassInfo getBean() {
        return bean;
    }

    public void setBean(ClassInfo bean) {
        this.bean = bean;
    }

    public List<AttributeDeclared> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<AttributeDeclared> attributes) {
        this.attributes = attributes;
    }
}
