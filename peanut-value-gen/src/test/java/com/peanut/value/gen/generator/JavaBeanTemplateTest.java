package com.peanut.value.gen.generator;

import com.peanut.reflect.model.ClassInfo;
import com.peanut.reflect.model.FieldInfo;
import com.peanut.reflect.model.MethodInfo;
import com.peanut.reflect.model.TypeInfo;
import com.peanut.reflect.util.ClassInfoCollector;
import com.peanut.value.gen.model.BeanSourceCodeDeclared;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Executable;
import java.lang.reflect.Parameter;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class JavaBeanTemplateTest {
    ClassInfoCollector classInfoCollector = new ClassInfoCollector();

    @Test
    public void test() {
        ClassInfo executable = classInfoCollector.read(ClassInfo.class);
        BeanSourceCodeDeclared beanDeclared = new BeanSourceCodeDeclaredBuilder().buildFromClassInfo(executable);
        JavaBeanTemplate javaBeanTemplate = new JavaBeanTemplate(beanDeclared);
        System.out.println(javaBeanTemplate.gen());
    }
    @Test
    public void testType(){
        ClassInfo executable = classInfoCollector.read(A.class);
        BeanSourceCodeDeclared beanDeclared = new BeanSourceCodeDeclaredBuilder().buildFromClassInfo(executable);
        JavaBeanTemplate javaBeanTemplate = new JavaBeanTemplate(beanDeclared);
        System.out.println(javaBeanTemplate.gen());
    }
    public static class A<T>{
        private Map<LocalDate, List<? extends T>> map;
        public Map<LocalDate, List<? extends T>> getMap(){
            return null;
        }
        public void setMap(Map<LocalDate, List<? extends T>> map){
            this.map = map;
        }
    }
}
