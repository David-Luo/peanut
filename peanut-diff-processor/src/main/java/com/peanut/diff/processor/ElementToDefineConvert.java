package com.peanut.diff.processor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import com.peanut.reflect.model.AnnotationInfo;
import com.peanut.reflect.model.ClassInfo;
import com.peanut.reflect.model.FieldInfo;
import com.peanut.reflect.model.MethodInfo;
import com.peanut.reflect.model.TypeInfo;
import com.peanut.reflect.model.ParameterInfo;

public class ElementToDefineConvert {
    private Elements elementUtils;
    private Types typeUtils;

    public ElementToDefineConvert(ProcessingEnvironment processingEnv){
        this.elementUtils = processingEnv.getElementUtils();
        this.typeUtils = processingEnv.getTypeUtils();
    }
    public ClassInfo elementToClassInfo(TypeElement e) {
        ClassInfo classDef = new ClassInfo(buildPackage((PackageElement) e.getEnclosingElement()),e.getSimpleName().toString());
        buildTypeVariables(e.getTypeParameters(),classDef);
        classDef.setModifiers(buildModifiers(new HashSet<Modifier>(e.getModifiers())));
        classDef.setIsInterface(e.getKind() == ElementKind.INTERFACE);
        classDef.setSuperClass(new TypeInfo(e.getSuperclass().toString()));
        classDef.setSuperInterfaces(buildSuperInterfaces(e));
        classDef.setAnnotations(buildAnnotations(e));
        
        List<FieldInfo> fields = e.getEnclosedElements().stream()
                .filter(o -> o.getKind() == ElementKind.FIELD)
                .map(o -> (VariableElement) o)
                .map(this::buiFieldInfo)
                .collect(Collectors.toList());
        classDef.setFields(fields);

        List<MethodInfo> methods = e.getEnclosedElements().stream()
                .filter(o -> o.getKind() == ElementKind.METHOD)
                .map(o -> (ExecutableElement) o)
                .map(this::buildMethod)
                .collect(Collectors.toList());
        classDef.setMethods(methods);
        return classDef;
    }

    private List<TypeInfo> buildSuperInterfaces(TypeElement type) {
        return type.getInterfaces().stream().map(this::buildTypeInfo).toList();
    }
    private TypeInfo buildTypeInfo(TypeMirror typeMirror){
        TypeInfo typeInfo;
        if(typeMirror instanceof TypeVariable){
            TypeVariable typeVariable = (TypeVariable) typeMirror;
            return new TypeInfo(typeVariable.toString());
        }else if(typeMirror instanceof DeclaredType){
            DeclaredType declaredType = (DeclaredType) typeMirror;
            // 获取声明此类型的元素（TypeElement）
            TypeElement typeElement = (TypeElement) declaredType.asElement();
            // 获取类名
            typeInfo = new TypeInfo(typeElement.getQualifiedName().toString());
            // 获取所有的类型参数（即泛型）
            List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
            for (TypeMirror typeArgument : typeArguments) {
                if (typeArgument instanceof TypeVariable) {
                    TypeVariable typeVar = (TypeVariable) typeArgument;
                    // 泛型变量的名字
                    String genericName = typeVar.asElement().getSimpleName().toString();
                    typeInfo.addGenericType(new TypeInfo(genericName));
                    // 可能需要进一步获取泛型变量的上界
                    // List<? extends TypeMirror> bounds = typeVar.getBounds();
                    // for (TypeMirror bound : bounds) {
                    //     // 这里是泛型变量的上界类型
                    //     String boundTypeName = bound.toString();
                    // }
                } else if(typeArgument instanceof DeclaredType){
                    typeInfo.addGenericType(new TypeInfo(((DeclaredType)typeArgument).toString()));
                }else {
                    // 其他类型的信息
                    String typeName = typeArgument.toString();
                }
            }
            return typeInfo;
        }else{
            return new TypeInfo(typeMirror.toString());
        }
        
    }

    protected MethodInfo buildMethod(ExecutableElement e) {
        MethodInfo m = new MethodInfo();
        m.setName(e.getSimpleName().toString());
        // m.setExceptions(new LinkedList<>(e.getThrownTypes())); TODO add exceptions
        m.setModifiers(buildModifiers(new HashSet<Modifier>(e.getModifiers())));
        m.setParameters(buildParameters(e.getParameters()));
        m.setReturnType(new TypeInfo(e.getReturnType().toString()));
        m.setAnnotations(buildAnnotations(e));
        
        // m.setTypeVariables(e.getTypeParameters().stream().map(TypeParameterElement::asType).map(o -> (TypeVariable) o).collect(Collectors.toList()));

        // m.setDefaultValue(defaultValue);e.getDefaultValue();
        // m.setCode(code);
        // m.setVarargs(varargs);
        // TypeMirror rt = e.getReceiverType();
        return m;
    }
    private void buildTypeVariables(List<? extends TypeParameterElement> parameter, TypeInfo classDef) {
        parameter.stream()
            .map(TypeParameterElement::asType)
            .map(o -> new TypeInfo(o.toString()))
            .forEach(t -> classDef.addGenericType(t));
    }
    private List<ParameterInfo> buildParameters(List<? extends VariableElement> parameter) {
        return parameter.stream().map(this::buildParameters).collect(Collectors.toList());
    }
    
    private ParameterInfo buildParameters(VariableElement element) {
        ParameterInfo parameterDef = new ParameterInfo(new TypeInfo(element.asType().toString()),element.getSimpleName().toString());
        parameterDef.setAnnotations(buildAnnotations(element));
        // Set<Modifier> modifiers = e.getModifiers(); TODO add modifiers
        parameterDef.setName(element.getSimpleName().toString());
        return parameterDef;
    }

    protected FieldInfo buiFieldInfo(VariableElement e) {
        FieldInfo fieldDef = new FieldInfo(new TypeInfo(e.asType().toString()),e.getSimpleName().toString());
        fieldDef.setAnnotations(buildAnnotations(e));
        fieldDef.setModifiers(buildModifiers(new HashSet<Modifier>(e.getModifiers())));
        return fieldDef;
    }

    private String buildPackage(PackageElement e) {
        if (e == null)
            return null;
        return e.getQualifiedName().toString();
    }

    private AnnotationInfo buildAnnotationInfo(AnnotationMirror mirror) {
        
        AnnotationInfo ann = new AnnotationInfo(mirror.getAnnotationType().toString());
        Map<? extends ExecutableElement, ? extends AnnotationValue> values = mirror.getElementValues();
        Map<String, Object> members = new HashMap<>(values.size());
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : values.entrySet()) {
            members.put(entry.getKey().getSimpleName().toString(), entry.getValue().getValue());
        }
        ann.setMembers(members);
        return ann;
    }

    private List<AnnotationInfo> buildAnnotations(Element element) {
        return element.getAnnotationMirrors().stream()
                .map(this::buildAnnotationInfo)
                .collect(Collectors.toList());
    }

    private int buildModifiers(Set<Modifier> modifiers) {
        int reflectionModifiers = 0;
        for (Modifier modifier : modifiers) {
            // 将每个 javax.lang.model.element.Modifier 映射到 java.lang.reflect.Modifier 的常量
            switch (modifier) {
                case PUBLIC:
                    reflectionModifiers |= java.lang.reflect.Modifier.PUBLIC;
                    break;
                case PROTECTED:
                    reflectionModifiers |= java.lang.reflect.Modifier.PROTECTED;
                    break;
                case PRIVATE:
                    reflectionModifiers |= java.lang.reflect.Modifier.PRIVATE;
                    break;
                case STATIC:
                    reflectionModifiers |= java.lang.reflect.Modifier.STATIC;
                    break;
                case FINAL:
                    reflectionModifiers |= java.lang.reflect.Modifier.FINAL;
                    break;
                case ABSTRACT:
                    reflectionModifiers |= java.lang.reflect.Modifier.ABSTRACT;
                    break;
                case TRANSIENT:
                    reflectionModifiers |= java.lang.reflect.Modifier.TRANSIENT;
                    break;
                case VOLATILE:
                    reflectionModifiers |= java.lang.reflect.Modifier.VOLATILE;
                    break;
                    case SYNCHRONIZED:
                    reflectionModifiers |= java.lang.reflect.Modifier.SYNCHRONIZED;
                    break;
                case NATIVE:
                    reflectionModifiers |= java.lang.reflect.Modifier.NATIVE;
                    break;
                case STRICTFP:
                    // reflectionModifiers |= java.lang.reflect.Modifier.;
                    break;
                default:
                    break;
            }
                
        }
        return reflectionModifiers;
    }
}
