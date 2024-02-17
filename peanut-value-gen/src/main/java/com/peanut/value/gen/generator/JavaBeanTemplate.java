package com.peanut.value.gen.generator;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.TypeParameter;
import com.peanut.reflect.model.*;
import com.peanut.reflect.util.ModifiersUtils;
import com.peanut.util.CollectionUtils;
import com.peanut.util.StringUtils;
import com.peanut.value.gen.model.AttributeDeclared;
import com.peanut.value.gen.model.BeanSourceCodeDeclared;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Stream;

public class JavaBeanTemplate {
    private CompilationUnit generate;
    private BeanSourceCodeDeclared beanDeclared;
    private ClassInfo bean;
    private ClassOrInterfaceDeclaration generateClass;

    public JavaBeanTemplate(BeanSourceCodeDeclared beanDeclared){
        this.beanDeclared = beanDeclared;
        this.bean = beanDeclared.getBean();
        this.generate = new CompilationUnit().setPackageDeclaration(bean.getPackageName());
        this.generateClass = generate.addClass(bean.getSimpleName());
    }

    public String gen(){
        createClassSymbol();
        createImports();
        createAttributes();

        createToString();
        createEquals();
        createHashCode();
        createClone();

        return generate.toString();
    }

    private void createClone() {
    }

    private void createHashCode() {
    }

    private void createEquals() {
    }

    private void createToString() {
    }

    private void createImports() {
        if (bean.getImports() == null) {
            return;
        }
        bean.getImports().forEach(this::createImport);
    }

    private void createImport(ImportInfo info) {
        if (StringUtils.isBlank(info.getPackageName())) {return;}
        Name checkerDeclaration = StaticJavaParser.parseName(info.getName());
        generate.addImport(new ImportDeclaration(checkerDeclaration, false, false));
    }

    private void createClassSymbol() {
        generateClass.setPublic(true)
                .addImplementedType(Serializable.class);
        if (bean.hasGeneric()){
            for (TypeInfo genericType : bean.getGenericTypes()) {
                generateClass.addTypeParameter(new TypeParameter(genericType.toTypeString()));
            }
        }
    }

    private void createAttributes(){
        if(CollectionUtils.isEmpty(beanDeclared.getAttributes())){
            throw new RuntimeException("JavaBean属性不能为空");
        }
        for (AttributeDeclared attribute: beanDeclared.getAttributes()){
            createField(attribute);
            createGetter(attribute);
            createSetter(attribute);
        }
    }

    private void createSetter(AttributeDeclared attribute) {
        MethodInfo method = attribute.getSetter();
        if (method == null){
            return;
        }
        MethodDeclaration setter = createMethodSymbol(method);
        BlockStmt body = new BlockStmt();
        String name = attribute.getField().getName();
        body.addStatement("this."+ name +"="+name+";");
        setter.setBody(body);
    }

    private void createGetter(AttributeDeclared attribute) {
        MethodInfo method = attribute.getGetter();
        if (method == null){
            return;
        }
        MethodDeclaration getter = createMethodSymbol(method);
        BlockStmt body = new BlockStmt();
        body.addStatement("return "+attribute.getField().getName()+";");
        getter.setBody(body);
    }
    private MethodDeclaration createMethodSymbol(MethodInfo info){
        String[] modifierArray = ModifiersUtils.getModifiers(info.getModifiers());
        if (modifierArray.length == 0) {
            throw new RuntimeException("method modifier is null:"+info.getName());
        }
        Modifier.Keyword[] modifiers = Stream.of(modifierArray)
                .map(String::toUpperCase)
                .map(com.github.javaparser.ast.Modifier.Keyword::valueOf)
                .toArray(Modifier.Keyword[]::new);
        MethodDeclaration method = generateClass
                .addMethod(info.getName(), modifiers)
                .setType(info.getReturnType().toTypeString());
        if (info.getParameters().size() > 0) {
            info.getParameters().forEach(parameter -> {
                method.addParameter(StaticJavaParser.parseType(parameter.getType().toTypeString()), parameter.getName());
            });

        }
        return method;
    }
    private void createField(AttributeDeclared attribute) {
        FieldInfo field = attribute.getField();
        FieldDeclaration fieldDeclaration = new FieldDeclaration();
        VariableDeclarator variable = new VariableDeclarator(StaticJavaParser.parseType(field.getType().toTypeString()), field.getName());
        if(!StringUtils.isBlank(field.getInitializer())){
            variable.setInitializer(field.getInitializer());
        }
        fieldDeclaration.getVariables().add(variable);
        fieldDeclaration.setPrivate(true);
        generateClass.getMembers().add(fieldDeclaration);
    }
}
