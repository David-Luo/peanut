package com.peanut.value.gen.model;

import com.peanut.reflect.model.FieldInfo;
import com.peanut.reflect.model.MethodInfo;

public class AttributeDeclared {
    private FieldInfo field;
    private MethodInfo getter;
    private MethodInfo setter;

    public FieldInfo getField() {
        return field;
    }

    public void setField(FieldInfo field) {
        this.field = field;
    }

    public MethodInfo getGetter() {
        return getter;
    }

    public void setGetter(MethodInfo getter) {
        this.getter = getter;
    }

    public MethodInfo getSetter() {
        return setter;
    }

    public void setSetter(MethodInfo setter) {
        this.setter = setter;
    }
}
