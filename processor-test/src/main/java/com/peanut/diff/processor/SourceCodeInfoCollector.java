package com.peanut.diff.processor;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.peanut.reflect.model.*;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SourceCodeInfoCollector {
    public Optional<ClassInfo> read(File sourceFile) {
        CompilationUnit compilationUnit = null;
        try {
            compilationUnit = StaticJavaParser.parse(sourceFile);
            return Optional.empty();
        } catch (FileNotFoundException e) {
            System.err.print("文件不存在" + sourceFile.getAbsolutePath());
            e.printStackTrace();
        }
        if (compilationUnit.getTypes().isEmpty()) {
            System.out.println(sourceFile.getAbsolutePath() + "找不到类声明");
            return Optional.empty();
        }
        ClassOrInterfaceDeclaration mainClass = (ClassOrInterfaceDeclaration) compilationUnit.getType(0);
        ClassInfo classInfo = new ClassInfo(new TypeInfo(mainClass.getName().asString()));
        classInfo.setIsInterface(mainClass.isInterface());
        classInfo.setAnnotations(buildAnnotations(mainClass.getAnnotations()));
        classInfo.setMethods(collectMethodDeclaration(mainClass));
        return Optional.of(classInfo);
    }

    private List<MethodInfo> collectMethodDeclaration(ClassOrInterfaceDeclaration mainClass) {
        return mainClass
                .getMethods()
                .stream()
                .map(this::buildMethod)
                .collect(Collectors.toList());
    }

    private MethodInfo buildMethod(MethodDeclaration declaration) {
        MethodInfo info = new MethodInfo();
        info.setName(declaration.getName().asString());
        info.setParameters(buildParameters(declaration.getParameters()));
        info.setAnnotations(buildAnnotations(declaration.getAnnotations()));
        info.setReturnType(new TypeInfo(declaration.getTypeAsString()));
        return info;
    }

    private List<ParameterInfo> buildParameters(NodeList<Parameter> pd) {
        return pd.stream().map(this::buildParameter).collect(Collectors.toList());
    }

    private ParameterInfo buildParameter(Parameter parameter) {
        ParameterInfo variable = new ParameterInfo(new TypeInfo(parameter.getTypeAsString()), parameter.getNameAsString());
        return variable;
    }

    private List<AnnotationInfo> buildAnnotations(NodeList<AnnotationExpr> nodeList) {
        return nodeList.stream().map(this::buildAnnotationInfo).collect(Collectors.toList());
    }

    private AnnotationInfo buildAnnotationInfo(AnnotationExpr a) {
        AnnotationInfo annotationInfo = new AnnotationInfo(a.getName().getIdentifier());
        List<Node> childNodes = a.getChildNodes();
        if (childNodes.size() < 2) {
            return annotationInfo;
        }
        for (int j = 1; j < childNodes.size(); j++) {
            Node node = childNodes.get(j);
            if (node instanceof MemberValuePair) {
                MemberValuePair pair = (MemberValuePair) node;
                annotationInfo.setMember(pair.getName().getIdentifier(), getAnnotationValue(pair.getValue()));
            } else {
                annotationInfo.setMember("value", getAnnotationValue((Expression) node));
            }

        }
        return annotationInfo;
    }


    private String getAnnotationValue(Expression expression) {
        if (expression instanceof StringLiteralExpr) {
            StringLiteralExpr s = (StringLiteralExpr) expression;
            return s.getValue();
        } else if (expression instanceof ClassExpr) {
            ClassExpr c = (ClassExpr) expression;
            String simpleName = ((ClassOrInterfaceType) c.getType()).getName().getIdentifier();
            return getFullName(expression, simpleName)
                    .orElse(simpleName);
        }

        return expression.toString();
    }

    public Optional<String> getFullName(Node node, String simpleName) {
        Optional<CompilationUnit> cu = node.findAncestor(CompilationUnit.class);
        if (!cu.isPresent()) {
            return Optional.empty();
        }

        CompilationUnit compilationUnit = cu.get();
        Optional<String> op = compilationUnit.getImports()
                .stream()
                .map(ImportDeclaration::getName)
                .map(Name::toString)
                .filter(s -> s.endsWith(simpleName))
                .findFirst();
        if (op.isPresent()) {
            return op;
        }
        String full = compilationUnit.getPackageDeclaration().get().getName().toString() + '.' + simpleName;
        return Optional.of(full);
    }
}
