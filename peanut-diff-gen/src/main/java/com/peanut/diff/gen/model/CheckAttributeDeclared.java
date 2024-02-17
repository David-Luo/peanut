package com.peanut.diff.gen.model;


import java.lang.reflect.Modifier;

import com.peanut.diff.runtime.ChangeChecker;
import com.peanut.reflect.model.FieldInfo;
import com.peanut.reflect.model.MethodInfo;
import com.peanut.reflect.model.TypeInfo;
import com.peanut.util.StringUtils;

public class CheckAttributeDeclared {
    //需要校验的属性
    private FieldInfo fieldBeCheck;
    //属性get方法
    private MethodInfo fieldGetter;
    //属性校验服务
    private FieldInfo checkerField;
    //属性校验方法
    private MethodInfo checkMethod;
    private CheckerStrategyDeclared strategy;
/**
 *  private String code; // fieldBeCheck
 *  private String getCode(){return code;} // fieldGetter
    private ChangeChecker codeChecker = getCodeChecker();//checkerField
    private ChangeChecker getUpdateTimeChecker() { // checkMethod
        return ServiceLocator.get("com.peanut.diff.runtime.checker.LocalDateTimeChecker"); // CheckerStrategy
    }
 */
    public CheckAttributeDeclared(FieldInfo fieldBeCheck,
                                MethodInfo fieldGetter,
                                CheckerStrategyDeclared strategy) {
        this.fieldBeCheck = fieldBeCheck;
        this.fieldGetter = fieldGetter;
        this.strategy = strategy;
        this.checkMethod = initCheckMethod(fieldBeCheck);
        this.checkerField = defaultCheckerField(fieldBeCheck);
    }

    public FieldInfo getFieldBeCheck() {
        return fieldBeCheck;
    }

    public MethodInfo getCheckMethod() {
        return checkMethod;
    }

    public void setCheckMethod(MethodInfo checkMethod) {
        this.checkMethod = checkMethod;
    }

    private String checkMethodName(String name) {
        return "get" + StringUtils.capitalize(name)+"Checker";
    }
    private MethodInfo initCheckMethod(FieldInfo beanField){
        MethodInfo methodInfo = new MethodInfo();
        methodInfo.setModifiers(Modifier.PRIVATE);
        methodInfo.setName(checkMethodName(beanField.getName()));
        methodInfo.setReturnType(new TypeInfo(ChangeChecker.class));
        return methodInfo;
    }

    private FieldInfo defaultCheckerField(FieldInfo beanField){
        return new FieldInfo(new TypeInfo(ChangeChecker.class),
            beanField.getName()+"Checker",
            checkMethod.getName() + "()");
    }

    public CheckerStrategyDeclared getStrategy() {
        return strategy;
    }

    public void setStrategy(CheckerStrategyDeclared strategy) {
        this.strategy = strategy;
    }

    public MethodInfo getFieldGetter() {
        return fieldGetter;
    }

    public FieldInfo getCheckerField() {
        return checkerField;
    }

    public void setCheckerField(FieldInfo attributeChecker) {
        this.checkerField = attributeChecker;
    }

}
