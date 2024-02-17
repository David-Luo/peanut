package com.peanut.diff.gen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.peanut.diff.gen.model.CheckerSourceCodeDeclared;
import com.peanut.diff.runtime.checker.ChangeCheckerInitialContext;
import com.peanut.util.SourceCodeUtil;

public class CheckerSourceCodeBuilderUtil {
    private CheckerSourceCodeDeclaredBuilder checkerDeclarationBuilder = new CheckerSourceCodeDeclaredBuilder();
    private CheckerSourceCodeTemplate sourceCodeBuilder = new CheckerSourceCodeTemplate();
    // private String gePath = "build/generated/main/java";
    public CheckerSourceCodeBuilderUtil() {
    }

    public String generateFromInterface(String interfaceName) throws ClassNotFoundException {
        CheckerSourceCodeDeclared declare = checkerDeclarationBuilder.buildFromInterface(Class.forName(interfaceName));
        return sourceCodeBuilder.gen(declare);
    }

    public String buildFormBeanBeCheck(String beanClass) throws ClassNotFoundException {
        CheckerSourceCodeDeclared declare = checkerDeclarationBuilder.buildFormBeanBeCheck(beanClass,null);
        return sourceCodeBuilder.gen(declare);
    }
    private String getSourcePath(String className, String gePath) {
        if (gePath == null || gePath.isEmpty()) {
            return className.replace(".", "/") + ".java";
        }
        int lastIndex =  className.lastIndexOf(".");
        String substring = className.substring(lastIndex);
        return  gePath + substring.replace(".", "/") + ".java";
    }

    public void scanAndGenerate(String sourcePath, String entityPackage,String interfacePackage) throws IOException {
        Map<String,CheckerSourceCodeDeclared> interfaceDeclaration = scanInterface(sourcePath,interfacePackage);
        Map<String,CheckerSourceCodeDeclared> entityDeclaration = scanBean(sourcePath,entityPackage);

        List<CheckerSourceCodeDeclared> unDeclared =entityDeclaration.entrySet().stream().filter( e -> !interfaceDeclaration.containsKey(e.getKey())).map(Map.Entry::getValue).collect(Collectors.toList());
        unDeclared.stream().forEach(c -> generate(sourcePath,c));
        interfaceDeclaration.values().forEach(c -> generate(sourcePath,c));

        Properties p = new Properties();
        Stream.of(unDeclared, interfaceDeclaration.values())
                .flatMap(Collection::stream)
                .forEach(d -> p.put(d.getBeanBeCheck().getName(), d.getCheckerImplClass().getName()));
        p.store(new FileWriter(sourcePath+"/"+ ChangeCheckerInitialContext.beanCheckerMapping),"generated");
    }

    public Map<String,CheckerSourceCodeDeclared> scanInterface(String sourcePath, String scanPackage) throws IOException {
        return SourceCodeUtil.findAllClassInPackage(sourcePath, scanPackage)
                .stream()
                .map(name -> {
                    try {
                        return Class.forName(name);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(checkerDeclarationBuilder::buildFromInterface)
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(this::getTargetClassName, Function.identity()));
    }

    private String getTargetClassName(CheckerSourceCodeDeclared bean){
        return bean.getCheckerImplClass().getName();
    }
    public void generate(String sourcePath, CheckerSourceCodeDeclared declare){
        File generated = getSourceFile(sourcePath, declare.getCheckerImplClass().getName());
        try (FileWriter fw = new FileWriter(generated);){
            String code = sourceCodeBuilder.gen(declare);
            fw.write(code);
            fw.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public Map<String,CheckerSourceCodeDeclared> scanBean(String sourcePath, String scanPackage) throws IOException {
        return SourceCodeUtil.findAllClassInPackage(sourcePath, scanPackage)
                .stream()
                .map(bean -> checkerDeclarationBuilder.buildFormBeanBeCheck(bean, null))
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(this::getTargetClassName, Function.identity()));
    }

    private String getSourcePath(String className) {
        return className.replace(".", "/") + ".java";
    }

    public File getSourceFile(String sourceRoot, String className) {
        return new File(sourceRoot, getSourcePath(className));
    }
}
