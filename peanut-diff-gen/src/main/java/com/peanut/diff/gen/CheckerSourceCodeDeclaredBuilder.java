package com.peanut.diff.gen;


import com.google.auto.service.AutoService;
import com.peanut.diff.gen.model.*;
import com.peanut.diff.runtime.Difference;
import com.peanut.diff.runtime.NamedDifference;
import com.peanut.diff.runtime.spi.ServiceLocator;
import com.peanut.reflect.util.ClassInfoCollector;
import com.peanut.reflect.model.*;
import com.peanut.diff.runtime.ChangeChecker;
import com.peanut.util.CollectionUtils;
import com.peanut.util.JavaBeanUtils;
import com.peanut.util.StringUtils;

import jakarta.persistence.Id;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
@SuppressWarnings({ "rawtypes"})
public class CheckerSourceCodeDeclaredBuilder {
    private ClassInfoCollector classInfoCollector = new ClassInfoCollector();

    public CheckerSourceCodeDeclared buildFormBeanBeCheck(String className,String packageName){
        ClassInfo beanBeCheck = classInfoCollector.read(className);
        TypeInfo interfaceType = new TypeInfo(ChangeChecker.class.getName());
        interfaceType.addGenericType(new TypeInfo(className));
        ClassInfo targetInterface=new ClassInfo(interfaceType);
        CheckerSourceCodeDeclared declaration = new CheckerSourceCodeDeclared(beanBeCheck,targetInterface);
        if (StringUtils.isBlank(packageName)){
            String beanPackage = beanBeCheck.getPackageName();
            packageName = beanPackage.substring(0,beanPackage.lastIndexOf("."))+".diff.impl";
        }

        String checkerImplClassName = beanBeCheck.getSimpleName()+"ChangeCheckerImpl";
        ClassInfo checkerImplClass = new ClassInfo(new TypeInfo(packageName,checkerImplClassName));
        checkerImplClass.addSuperInterface(interfaceType);
        TypeInfo checkerServiceSymbol=new TypeInfo(ChangeChecker.class);
        checkerServiceSymbol.addGenericType(beanBeCheck);
        checkerImplClass.addSuperInterface(checkerServiceSymbol);
        declaration.setCheckerImplClass(checkerImplClass);
        declaration.setAttributes(buildAttributeChecker(beanBeCheck));
        buildImports(declaration);
        addAnnotation(checkerImplClass);
        return declaration;
    }
    private void addAnnotation(ClassInfo checkerImplClass){
        AnnotationInfo annotation=new AnnotationInfo(AutoService.class.getName());
        annotation.setMember("value",ChangeChecker.class.getName());
        checkerImplClass.addAnnotation(annotation);
    }
    private void buildImports(CheckerSourceCodeDeclared declaration){
        ClassInfo checkerImplClass = declaration.getCheckerImplClass();
        checkerImplClass.addImport(declaration.getBeanBeCheck());
        checkerImplClass.addImport(declaration.getCheckerInterface());
        checkerImplClass.addImport(new TypeInfo(Difference.class));
        checkerImplClass.addImport(new TypeInfo(ChangeChecker.class));
        checkerImplClass.addImport(new TypeInfo(Optional.class));
        checkerImplClass.addImport(new TypeInfo(Map.class));
        checkerImplClass.addImport(new TypeInfo(HashMap.class));
        checkerImplClass.addImport(new TypeInfo(NamedDifference.class));
        checkerImplClass.addImport(new TypeInfo(ServiceLocator.class));
        for ( CheckAttributeDeclared attribute : declaration.getAttributes()){
            TypeInfo type = attribute.getFieldBeCheck().getType();
            if(type.hasGeneric() && type.isNotInnerClass()){
                for (TypeInfo generic : type.getGenericTypes()){
                    if(generic.isNotInnerClass()){
                        checkerImplClass.addImport(generic);
                    }
                }
            }
        }
    }
    
    public CheckerSourceCodeDeclared buildFromInterface(Class interfaceDeclaration){
        ClassInfo targetInterface = classInfoCollector.read(interfaceDeclaration);
        return buildFromInterfaceInfo(targetInterface);
    }

    public CheckerSourceCodeDeclared buildFromInterfaceInfo(ClassInfo targetInterface){
        String interfaceDeclaration = targetInterface.getName();
        if(!targetInterface.isInterface()){
            System.out.println(interfaceDeclaration +"不是继承ChangeChecker的接口");
            return null;
        }
        Optional<TypeInfo> checker = targetInterface.getSuperInterface(ChangeChecker.class);
        if(!checker.isPresent()){
            System.out.println(interfaceDeclaration +"不是继承ChangeChecker的接口");
            return null;
        }
        TypeInfo interfaceType = checker.get();
        TypeInfo targetBeanType = interfaceType.getFirstGenericType();
        if(targetBeanType==null){
            System.out.println(interfaceDeclaration +"接口必须声明具体的比较对象");
            return null;
        }

        ClassInfo beanBeCheck = classInfoCollector.read(targetBeanType.getName());
        ClassInfo checkerImplClass = new ClassInfo(targetInterface.getPackageName()+".impl",targetInterface.getSimpleName()+"Impl");
        checkerImplClass.addSuperInterface(targetInterface);
        CheckerSourceCodeDeclared declaration = new CheckerSourceCodeDeclared(beanBeCheck,targetInterface,checkerImplClass);
        declaration.setAttributes(buildAttributeChecker(beanBeCheck));
        overwriteCheckerMethod(declaration);
        buildImports(declaration);
        return declaration;
    }

    private Map<String, MethodInfo> collectMethodDeclaration(ClassInfo interfaceDeclaration){
        return interfaceDeclaration
                .getMethods()
                .stream()
                .collect(Collectors.toMap(MethodInfo::getName, Function.identity()));
    }

    private List<CheckAttributeDeclared> buildAttributeChecker(ClassInfo targetBean){
        List<CheckAttributeDeclared> attributes = new ArrayList<>();
        for (FieldInfo filed : targetBean.getFields()){
            attributes.add(new CheckAttributeDeclared(filed,
                                    JavaBeanUtils.findGetter(targetBean,filed),
                                    createCheckerStrategyDeclared(filed)));
        }
        return attributes;
    }

    private CheckerStrategyDeclared createCheckerStrategyDeclared(FieldInfo filed) {
        CheckerStrategyDeclared strategyDeclared=null;
        if (CollectionUtils.isCollection(filed.getType().getName())) {
            strategyDeclared = getCollectionChecker(filed);
        }
        if (strategyDeclared != null) {
            return strategyDeclared;
        }
        String fieldChecker = ServiceLocator.context.getBeanChecker(filed.getType().getName());
        if(fieldChecker!=null){
            return new CheckerStrategyDeclared(filed.getType(),new TypeInfo(fieldChecker));
        }
        return new CheckerStrategyDeclared(filed.getType());
    }
    private CheckerStrategyDeclared getCollectionChecker(FieldInfo filed){

        List<TypeInfo> genericTypes = filed.getType().getGenericTypes();
        if (genericTypes.size() != 1) {
            System.out.println("无法匹配元素类型："+filed.getName());
            return null;
        }
        TypeInfo elementType = genericTypes.get(0);
        boolean isJavaBean = false;
        try {
            isJavaBean = JavaBeanUtils.isJavaBean(elementType.getName());
        } catch (Exception e) {
            System.out.println("无法匹配元素类型："+filed.getName());
            return null;
        }
        
        if(!isJavaBean){
            return null;
        }
        
        Field[] fields = getBeanId(elementType.getName());
        if(fields.length == 1){
            return new CheckerStrategyDeclared(filed.getType(),fields[0].getName());
        }
        //TODO 处理多个Id的情况
        return null;
    }
    
    public static Field[] getBeanId(String beanClassName){
        Class<?> clazz;
        try {
            clazz = Class.forName(beanClassName);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("找不到类："+beanClassName);
        }
        
        List<Field> idFieldsList = new ArrayList<>();
        while (clazz.getName() != Object.class.getName()) {
            getBeanId(clazz, idFieldsList);
            clazz = clazz.getSuperclass();
        }
        
        return idFieldsList.toArray(new Field[idFieldsList.size()]);
    }
    private static void getBeanId(Class<?> clazz, List<Field> idFieldsList) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                // 检查到@Id注解
                idFieldsList.add(field);
            }
        }
    }

/*
    private String getClassChecker(String className){
        String checkerName = ServiceLocator.context.getBeanChecker(className);
        if(checkerName!=null){
            return checkerName;
        }
        if (JavaBeanUtils.isJavaBean(className)) {
            createNested(className);
            return ServiceLocator.context.getBeanChecker(className);
        }
        throw new IllegalArgumentException("找不到Checker:"+className);
    }

    private void createNested(String className) {
        if(processed.contains(className)){
            return ;
        }else {
            processed.add(className);
        }
        CheckerSourceCodeDeclared checkerSourceCodeDeclared = buildFormBeanBeCheck(className);
        String checkerName = checkerSourceCodeDeclared.getCheckerImplClass().getName();
        ServiceLocator.context.register(className, checkerName);
        checkerServiceMap.put(checkerName, checkerSourceCodeDeclared);
    }
 */
    /**
     * 用于获取给定修饰符的非抽象修饰符
     * @param modifier 给定的修饰符
     * @return 非抽象修饰符
     */
    private int nonAbstractMask(int modifier){
        return modifier & ~Modifier.ABSTRACT;
    }
    private void overwriteCheckerMethod(CheckerSourceCodeDeclared declaration){
        Map<String, MethodInfo> methodDeclaration = collectMethodDeclaration(declaration.getCheckerInterface());
        for (CheckAttributeDeclared attributeDeclaration : declaration.getAttributes()){
            String methodName = attributeDeclaration.getCheckMethod().getName();
            if(!methodDeclaration.containsKey(methodName)){
                continue;
            }
            MethodInfo checkMethod = methodDeclaration.get(attributeDeclaration.getCheckMethod().getName());
                checkMethod.setModifiers(nonAbstractMask(checkMethod.getModifiers()));
                attributeDeclaration.setCheckMethod(checkMethod);
                initDeclaredStrategy(attributeDeclaration);
        }
    }

    private void initDeclaredStrategy(CheckAttributeDeclared declaration){
        List<AnnotationInfo> annotations = declaration.getCheckMethod().getAnnotations();
        if(annotations.isEmpty()){
            throw new RuntimeException("请声明检查策略");
        }

        Map<String,Object> info = annotations.get(0).getMembers();
        String key = (String)info.get("key");
        Class affirmativeChecker = (Class) info.get("value");
        if(StringUtils.isBlank(key)){
            declaration.setStrategy(new CheckerStrategyDeclared(declaration.getFieldBeCheck().getType(),new TypeInfo(affirmativeChecker)));
        }else{
            declaration.setStrategy(new CheckerStrategyDeclared(declaration.getFieldBeCheck().getType(),key));
        }
    }
}
