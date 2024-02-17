package com.peanut.reflect.model;

import java.util.*;
import java.util.stream.Collectors;

public class ClassInfo extends TypeInfo{
    private int modifiers;
    private boolean isInterface;
    private List<TypeInfo> superInterfaces;
    private List<ImportInfo> imports;
    private TypeInfo superClass;
    private String classComment;
    private List<FieldInfo> fields;
    private List<MethodInfo> methods;
    private List<AnnotationInfo> annotations;

    public ClassInfo(){
        superInterfaces = new LinkedList<>();
    }
    public ClassInfo(String classPackage, String simpleName){
        super(classPackage,simpleName);
        superInterfaces = new LinkedList<>();
    }
    public ClassInfo(TypeInfo type){
        this(type.getPackageName(),type.getSimpleName());
    }

    public void addSuperInterface(TypeInfo info) {
        this.superInterfaces.add(info);
    }

    public String getClassComment() {
        return classComment;
    }

    public void setClassComment(String classComment) {
        this.classComment = classComment;
    }

    public List<FieldInfo> getFields() {
        if (fields == null || fields.size() == 0) {
            return null;
        }
        return new ArrayList<>(fields.stream()
                .filter(field -> !"serialVersionUID".equals(field.getName()))
                .collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(FieldInfo::getName)))));
    }

    public Optional<FieldInfo> getField(String name){
        return fields.stream().filter(field -> field.getName().equals(name)).findFirst();
    }
    public List<MethodInfo> getMethods(String name) {
        return methods.stream().filter(method -> method.getName().equals(name)).collect(Collectors.toList());
    }
    public void setFields(List<FieldInfo> fields) {
        this.fields = fields;
    }
    public void addField(FieldInfo fieldInfo){
            if(fields==null){
                fields = new LinkedList<>();
            }
            fields.add(fieldInfo);
    }
    public List<ImportInfo> getImports() {
        return imports;
    }

    public void setImports(List<ImportInfo> imports) {
        this.imports = imports;
    }
    public void addImport(TypeInfo info){
        if(imports==null){
            imports = new LinkedList<>();
        }
        imports.add(new ImportInfo(info.getName()));
    }
    public List<MethodInfo> getMethods() {
        return methods;
    }

    public void setMethods(List<MethodInfo> methods) {
        this.methods = methods;
    }

    public void addMethod(MethodInfo methodInfo){
        if(methods==null){
            methods = new LinkedList<>();
        }
        methods.add(methodInfo);
    }
    public List<TypeInfo> getSuperInterfaces() {
        return superInterfaces;
    }

    public void setSuperInterfaces(List<TypeInfo> superInterfaces) {
        this.superInterfaces = superInterfaces;
    }


    public TypeInfo getSuperClass() {
        return superClass;
    }

    public void setSuperClass(TypeInfo superClass) {
        this.superClass = superClass;
    }

    public List<AnnotationInfo> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<AnnotationInfo> annotations) {
        this.annotations = annotations;
    }
    public void addAnnotation(AnnotationInfo info){
        if(annotations==null){
            annotations = new LinkedList<>();
        }
        annotations.add(info);
    }
    public Optional<TypeInfo> getSuperInterface(Class<?> interfaceName){
        if(superInterfaces==null) {
            return Optional.empty();
        }
        String className = interfaceName.getName();
        return superInterfaces.stream().filter( o -> o.getName().equals(className)).findFirst();
    }
    public int getModifiers() {
        return modifiers;
    }
    public void setModifiers(int modifiers) {
        this.modifiers = modifiers;
    }
    public boolean isInterface(){
        return this.isInterface;
    }
    public void setIsInterface(boolean isInterface) {
        this.isInterface = isInterface;
    }
}
