package com.peanut.reflect.util;

import com.peanut.reflect.model.ClassInfo;

public class ClassInfoCollector {
    public ClassInfo read(Class<?> clazz){
        ClassDeclaredVisitor visitor = new ClassDeclaredVisitor();
        JavaClassWalker walker = new JavaClassWalker();
        walker.walk(clazz,visitor);
        return visitor.getClassInfo();
    }

    public ClassInfo read(String className){
        try {
            return read(
                    Class.forName(className)
            );
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
