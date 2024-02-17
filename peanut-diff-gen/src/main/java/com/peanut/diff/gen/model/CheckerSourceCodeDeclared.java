package com.peanut.diff.gen.model;

import com.peanut.reflect.model.ClassInfo;

import java.util.List;

public class CheckerSourceCodeDeclared {
    private ClassInfo beanBeCheck;
    private ClassInfo checkerInterface;
    private ClassInfo checkerImplClass;
    private List<CheckAttributeDeclared> attributes;

    public CheckerSourceCodeDeclared(ClassInfo beanBeCheck, ClassInfo checkerInterface) {
        this.beanBeCheck = beanBeCheck;
        this.checkerInterface = checkerInterface;
    }

    public CheckerSourceCodeDeclared(ClassInfo beanBeCheck, ClassInfo checkerInterface, ClassInfo checkerImplClass) {
        this.beanBeCheck = beanBeCheck;
        this.checkerInterface = checkerInterface;
        this.checkerImplClass = checkerImplClass;
    }

    public ClassInfo getCheckerImplClass() {
        return checkerImplClass;
    }

    public void setCheckerImplClass(ClassInfo checkerImplClass) {
        this.checkerImplClass = checkerImplClass;
    }

    public ClassInfo getCheckerInterface() {
        return checkerInterface;
    }

    public ClassInfo getBeanBeCheck() {
        return beanBeCheck;
    }

    public List<CheckAttributeDeclared> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<CheckAttributeDeclared> attributes) {
        this.attributes = attributes;
    }
}
