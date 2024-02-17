package com.peanut.diff.gen.model;

import com.peanut.reflect.model.TypeInfo;

public class CheckerStrategyDeclared {

    private GetCheckerStrategy checkerStrategy;
    private TypeInfo checkerService;

    private TypeInfo attributeBeCheck;
    private String key;
    public CheckerStrategyDeclared(TypeInfo attributeBeCheck, String key) {
        this.attributeBeCheck = attributeBeCheck;
        this.key = key;
        this.checkerStrategy = GetCheckerStrategy.ByInnerClass;
    }
    public CheckerStrategyDeclared(TypeInfo attributeBeCheck, TypeInfo checkerService) {
        this.checkerService = checkerService;
        this.attributeBeCheck = attributeBeCheck;
        this.checkerStrategy = GetCheckerStrategy.ByCheckerServiceName;
    }
    public CheckerStrategyDeclared(TypeInfo attributeBeCheck) {
        this.attributeBeCheck = attributeBeCheck;
        this.checkerStrategy = GetCheckerStrategy.ByAttributeClassName;
    }

    public TypeInfo getCheckerService() {
        return checkerService;
    }
    public GetCheckerStrategy getCheckerStrategy() {
        return checkerStrategy;
    }

    public TypeInfo getAttributeBeCheck() {
        return attributeBeCheck;
    }

    public String getKey() {
        return key;
    }

    public enum GetCheckerStrategy{
        ByAttributeClassName,
        ByCheckerServiceName,
        ByInnerClass
    }
}
