package com.peanut.diff.processor;

import com.google.auto.service.AutoService;
import com.peanut.diff.gen.CheckerSourceCodeDeclaredBuilder;
import com.peanut.diff.gen.CheckerSourceCodeTemplate;
import com.peanut.diff.gen.model.CheckerSourceCodeDeclared;
import com.peanut.reflect.model.ClassInfo;
import com.peanut.util.JavaBeansFinder;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@SupportedAnnotationTypes("com.peanut.processor.annotation.AutoImplement")
public class CheckerServiceInterfaceProcessor  extends AbstractProcessor {
    private ElementToDefineConvert elementToDefineConvert=null;
    private CheckerSourceCodeDeclaredBuilder beanDeclarationBuilder = new CheckerSourceCodeDeclaredBuilder();
    private CheckerSourceCodeTemplate sourceCodeBuilder = new CheckerSourceCodeTemplate();
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        elementToDefineConvert = new ElementToDefineConvert(processingEnv);
         if(roundEnv.processingOver()){
             return false;
         }
         for (TypeElement annotation : annotations) {
             Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);
             for (Element element : elements) {
                if (element.getKind() != ElementKind.INTERFACE) {
                    error("The annotation @AutoImplement can only be applied on interfaces: ",
                            element);
                    continue;
            }
                 handle(annotation, element);
             }
         }
         return false;
    }

    public boolean handle(Element annotation, Element element) {
        ClassInfo info = elementToDefineConvert.elementToClassInfo((TypeElement)element);
        CheckerSourceCodeDeclared checkerDeclaration = beanDeclarationBuilder.buildFromInterfaceInfo(info);
        generateClass(checkerDeclaration,element);
        buildNested(checkerDeclaration.getBeanBeCheck().getPackageName(),
                checkerDeclaration.getCheckerImplClass().getPackageName(),
                element);
        return false;
    }
    private void buildNested(String beanPackage,String implPackage, Element ele){
        try {
            Set<String> beans = JavaBeansFinder.findJavaBeansInPackage(beanPackage);
            beans.forEach(bean -> {
                generateClass(beanDeclarationBuilder.buildFormBeanBeCheck(bean,implPackage),ele);
            });
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }
    private void error(String msg, Element e) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, msg, e);
    }    
    private void generateClass(CheckerSourceCodeDeclared checkerDeclaration,Element element) {
        String className = checkerDeclaration.getCheckerImplClass().getName();
         String sourceCode = sourceCodeBuilder.gen(checkerDeclaration);
        JavaFileObject sourceFile;
        try {
            sourceFile = processingEnv.getFiler().createSourceFile(className);
            Writer writer = sourceFile.openWriter();
            writer.write(sourceCode);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "生成代码失败"+className, element);
        }
    }
}
