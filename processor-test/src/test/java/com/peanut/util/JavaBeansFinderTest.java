package com.peanut.util;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JavaBeansFinderTest {
    @Test
    public void javaBeanValidatorTest(){
        try {
            Set<String> beans = JavaBeansFinder.findJavaBeansInPackage("com.peanut.reflect.model");
            assertEquals(7, beans.size());
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    public void reflectTest(){
        try {
            Set<String> beans = JavaBeansFinder.findJavaBeansInPackage(Method.class.getPackageName());

            beans.stream().forEach(System.out::println);
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
