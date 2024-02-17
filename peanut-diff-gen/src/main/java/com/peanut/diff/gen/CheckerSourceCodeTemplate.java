package com.peanut.diff.gen;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import com.google.auto.service.AutoService;
import com.peanut.diff.gen.model.*;
import com.peanut.diff.runtime.ChangeChecker;
import com.peanut.diff.runtime.checker.CollectionToMapDiffChecker;
import com.peanut.reflect.model.*;
import com.peanut.reflect.util.ModifiersUtils;
import com.peanut.util.CollectionUtils;
import com.peanut.util.JavaBeanUtils;
import com.peanut.util.StringUtils;

public class CheckerSourceCodeTemplate {

    private CompilationUnit generate;
    private ClassOrInterfaceDeclaration generateClass;

    public String gen(CheckerSourceCodeDeclared declare) {
        ClassInfo checkerImplClass = declare.getCheckerImplClass();
        this.generate = new CompilationUnit()
                .setPackageDeclaration(checkerImplClass.getPackageName());
        this.generateClass = createClassDeclaration(generate, checkerImplClass);
        addImports(checkerImplClass);
        createFields(declare.getAttributes());
        createCheckMethod(declare);
        createMainCheck(declare);
        return generate.toString();
    }
    private void addImports(ClassInfo checkerImplClass) {
        checkerImplClass.getImports().forEach(this::createImport);
    }

    private ClassOrInterfaceDeclaration createClassDeclaration(CompilationUnit generate, ClassInfo checkerImplClass) {
        ClassOrInterfaceDeclaration generateClass = generate
                .addClass(checkerImplClass.getSimpleName())
                .addImplementedType(checkerImplClass.getSuperInterfaces().get(0).toTypeString())//TODO add implement interface
                .setPublic(true);

//        addAnnotation(null,generateClass);
        return generateClass;
    }

    private void addAnnotation(AnnotationInfo annotationInfo,ClassOrInterfaceDeclaration generateClass) {
        // 创建注解表达式
        NormalAnnotationExpr annotation = new NormalAnnotationExpr();
        annotation.setName(new Name(AutoService.class.getName())); 
        annotation.addPair("value", ChangeChecker.class.getName()+".class");
        // 添加注解到类上
        generateClass.addAnnotation(annotation);
    }
    private void createImport(ImportInfo info) {
        if (info.getPackageName() == null) {return;}
        Name checkerDeclaration = StaticJavaParser.parseName(info.getName());
        generate.addImport(new ImportDeclaration(checkerDeclaration, false, false));
    }
    private void createCheckMethod(CheckerSourceCodeDeclared declare) {
        if (!Objects.isNull(declare) && !CollectionUtils.isEmpty(declare.getAttributes())) {
            declare.getAttributes().stream().forEach(this::createCheckGetter);
        }
    }

    private void createMainCheck(CheckerSourceCodeDeclared clazz) {
        MethodDeclaration generateMethod = generateClass.addMethod("check")
                .setPublic(true)
                .addAnnotation(Override.class)
                .setType(String.format("Optional<Difference<%s>>",clazz.getBeanBeCheck().getSimpleName()));

        BlockStmt body = createMainCheckBody(clazz.getAttributes());
        generateMethod.setBody(body);

        generateMethod.addParameter(clazz.getBeanBeCheck().getSimpleName(), "left")
                .addParameter(clazz.getBeanBeCheck().getSimpleName(), "right");
    }

    private BlockStmt createMainCheckBody(List<CheckAttributeDeclared> attributeCheckInfos) {
        BlockStmt body = new BlockStmt();
        if (CollectionUtils.isEmpty(attributeCheckInfos)) {
           body.addStatement("return Optional.empty();");
           return body;
        }
        body.addStatement("if(left == null && right ==null){return Optional.empty();}");
        body.addStatement("if(left !=null && right == null){return Optional.of(new NamedDifference(left,right,Difference.ChangeType.del));}");
        body.addStatement("if (left == null && right != null){return Optional.of(new NamedDifference(left,right,Difference.ChangeType.add));}");
        body.addStatement("Map<String, Difference> attributeChange=new HashMap<>();");
        body.addStatement("Optional<Difference> attributeDiff;");

        String diffStatement = "attributeDiff = %s.check(left.%s(), right.%s());";
        String putStatement = "if(attributeDiff.isPresent()){attributeChange.put(\"%s\", attributeDiff.get());}";
        for (CheckAttributeDeclared m : attributeCheckInfos) {
            body.addStatement(String.format(diffStatement, m.getCheckerField().getName(), m.getFieldGetter().getName(), m.getFieldGetter().getName()));
            body.addStatement(String.format(putStatement, m.getFieldBeCheck().getName()));
        }
        body.addStatement("if(attributeChange.isEmpty()){return Optional.empty();}");
        body.addStatement("return Optional.of(new NamedDifference(left, right,attributeChange));");
        return body;
    }

    private void createFields(List<CheckAttributeDeclared> fieldInfos) {
        if (!CollectionUtils.isEmpty(fieldInfos)) {
            fieldInfos.stream()
                    .map(CheckAttributeDeclared::getCheckerField)
                    .map(this::createField)
                    .distinct()
                    .forEach(f -> generateClass.getMembers().add(f));
        }
    }

    private FieldDeclaration createField(FieldInfo checkerField) {
        FieldDeclaration fieldDeclaration = new FieldDeclaration();
        VariableDeclarator variable = new VariableDeclarator(StaticJavaParser.parseType(checkerField.getType().getSimpleName()), checkerField.getName());
        if(!StringUtils.isBlank(checkerField.getInitializer())){
            variable.setInitializer(checkerField.getInitializer());
        }
        
        fieldDeclaration.getVariables().add(variable);
        fieldDeclaration.setPrivate(true);
        return fieldDeclaration;
    }

    // private void tryAddImportStatic(String name) {
    //     Name checkerDeclaration = StaticJavaParser.parseName(name);
    //     generate.addImport(new ImportDeclaration(checkerDeclaration, true, false));
    // }

    private MethodDeclaration createCheckGetterMethod(MethodInfo info){
        String[] modifierArray = ModifiersUtils.getModifiers(info.getModifiers());
        if (modifierArray.length == 0) {
            throw new RuntimeException("method modifier is null:"+info.getName());
        }
        Keyword[] modifiers = Stream.of(modifierArray)
                .map(String::toUpperCase)
                .map(com.github.javaparser.ast.Modifier.Keyword::valueOf)
                .toArray(Keyword[]::new);
        MethodDeclaration generateMethod = generateClass
                .addMethod(info.getName(), modifiers)
                .setType(info.getReturnType().toTypeString());
        return generateMethod;
    }

    private void createCheckGetter(CheckAttributeDeclared attributeCheckInfo) {
        
        MethodDeclaration generateMethod = createCheckGetterMethod(attributeCheckInfo.getCheckMethod());

        BlockStmt body = new BlockStmt();
        Expression expression;
        CheckerStrategyDeclared strategy = attributeCheckInfo.getStrategy();
        switch (strategy.getCheckerStrategy())
            {
                case ByCheckerServiceName:
                    expression = buildGetCheckerByName(strategy.getCheckerService());
                    break;
                case ByInnerClass:
                    expression = buildInnerClassChecker(attributeCheckInfo);
                    break;
                case ByAttributeClassName:
                    expression = buildGetCheckerByBean(strategy.getAttributeBeCheck());
                    break;
                default:
                    throw new IllegalArgumentException("strategy not support"+strategy.getCheckerStrategy());
            }

        body.addStatement(new ReturnStmt(expression));
        generateMethod.setBody(body);
    }

    private Expression buildGetCheckerByName(TypeInfo typeInfo){
        String template = "ServiceLocator.get(\"%s\")";
        String format = String.format(template,typeInfo.getName());
        return StaticJavaParser.parseExpression(format);
    }
    private Expression buildGetCheckerByBean(TypeInfo typeInfo) {
        String template = "ServiceLocator.getBeanChecker(\"%s\")";
        String format = String.format(template, typeInfo.getName());
        return StaticJavaParser.parseExpression(format);
    }

    /**
     * TODO fix this
     * @param attributeCheckInfo
     * @return
     */
    private Expression buildInnerClassChecker(CheckAttributeDeclared attributeCheckInfo) {
        CheckerStrategyDeclared strategy = attributeCheckInfo.getStrategy();

        ObjectCreationExpr listChecker = new ObjectCreationExpr();
        createImport(new ImportInfo(CollectionToMapDiffChecker.class.getName()));
        TypeInfo returnType = new TypeInfo(CollectionToMapDiffChecker.class);
        TypeInfo eleType = attributeCheckInfo.getFieldBeCheck().getType().getFirstGenericType();
        returnType.addGenericType(eleType);
        listChecker.setType(returnType.toTypeString());
        
        String getter = JavaBeanUtils.getterName(strategy.getKey());
        listChecker.addAnonymousClassBody(implementsKeyMapper(getter, eleType.getSimpleName()));
        return listChecker;
    }

    private MethodDeclaration implementsKeyMapper(String getter, String elementType) {
        MethodDeclaration methodDeclaration = new MethodDeclaration();
        methodDeclaration.setName("keyMapper");
        methodDeclaration.setType(Object.class);
        methodDeclaration.setProtected(true);
        methodDeclaration.addParameter(elementType, "source");
        BlockStmt body = new BlockStmt();
        methodDeclaration.setBody(body);
        methodDeclaration.addAnnotation(Override.class);
        body.addStatement("return source." + getter + "();");
        return methodDeclaration;
    }


}
