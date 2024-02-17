package com.peanut.diff.processor;


import javax.tools.ToolProvider;

public class CheckerServiceInterfaceProcessorTest {
    
    public static void main(String[] args) {
        compile(ClassInfoChecker.class);
    }
    private static void compile(Class<?> clazz){
        String className = clazz.getName().replaceAll("\\.","/");
        // 获取java编译器
        javax.tools.JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        String[] ops = {
                "-processor", CheckerServiceInterfaceProcessor.class.getName(),
                "-d", "processor-test/build/generated/sources/annotationProcessor/java/main",
                "processor-test/src/main/java/"+className+".java"};
        int i = javaCompiler.run(null, null, null, ops);
        System.out.println(className+"编译完成："+i);
    }

}
