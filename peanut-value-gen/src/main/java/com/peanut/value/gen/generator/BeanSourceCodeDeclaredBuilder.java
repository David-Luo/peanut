package com.peanut.value.gen.generator;

import com.peanut.reflect.model.ClassInfo;
import com.peanut.reflect.model.FieldInfo;
import com.peanut.reflect.model.MethodInfo;
import com.peanut.reflect.model.TypeInfo;
import com.peanut.util.JavaBeanUtils;
import com.peanut.value.gen.model.AttributeDeclared;
import com.peanut.value.gen.model.BeanSourceCodeDeclared;

import java.util.*;

public class BeanSourceCodeDeclaredBuilder {
    public BeanSourceCodeDeclared buildFromClassInfo(ClassInfo classInfo) {
        BeanSourceCodeDeclared beanDeclared = new BeanSourceCodeDeclared();
        beanDeclared.setBean(classInfo);
        beanDeclared.setAttributes(buildAttributes(classInfo));
        buildImports(classInfo);
        return beanDeclared;
    }

    private void buildImports(ClassInfo bean){
            for ( FieldInfo field : bean.getFields()){
                TypeInfo type = field.getType();
                bean.addImport(type);
                if(type.hasGeneric() && type.isNotInnerClass()){
                    for (TypeInfo generic : type.getGenericTypes()){
                        if(generic.isNotInnerClass()){
                            bean.addImport(generic);
                        }
                    }
                }
            }

    }
    private List<AttributeDeclared> buildAttributes(ClassInfo classInfo) {
        List<AttributeDeclared> attributes = new ArrayList<>();
        for (FieldInfo filed : classInfo.getFields()){
            AttributeDeclared declared = new AttributeDeclared();
            declared.setField(filed);
            declared.setGetter(JavaBeanUtils.findGetter(classInfo, filed));
            MethodInfo setter = JavaBeanUtils.findSetter(classInfo, filed);
            if (setter != null) {
                setter.getParameters().get(0).setName(filed.getName());
            }
            declared.setSetter(setter);
            attributes.add(declared);
        }
        return attributes;
    }
}
