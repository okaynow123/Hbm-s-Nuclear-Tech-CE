package com.hbm.processor;

import com.google.auto.service.AutoService;
import com.hbm.interfaces.AutoRegister;
import com.squareup.javapoet.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.*;

@AutoService(Processor.class)
@SupportedAnnotationTypes("com.hbm.interfaces.AutoRegister")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SuppressWarnings("unused")
public class TileEntityProcessor extends AbstractProcessor {
    private final Map<String, String> tileEntitiesToRegister = new LinkedHashMap<>();
    private Filer filer;
    private Messager messager;
    private Elements elementUtils;
    private Types typeUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.filer = processingEnv.getFiler();
        this.messager = processingEnv.getMessager();
        this.elementUtils = processingEnv.getElementUtils();
        this.typeUtils = processingEnv.getTypeUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(AutoRegister.class)) {
            if (!(element instanceof TypeElement)) {
                messager.printMessage(Diagnostic.Kind.ERROR, "Annotation @AutoRegisterTE can only be applied to classes.", element);
                continue;
            }
            TypeElement typeElement = (TypeElement) element;
            if (typeElement.getModifiers().contains(Modifier.ABSTRACT)) {
                messager.printMessage(Diagnostic.Kind.WARNING, "Skipping abstract class for registration.", element);
                continue;
            }
            TypeElement tileEntityElement = elementUtils.getTypeElement("net.minecraft.tileentity.TileEntity");
            if (!typeUtils.isSubtype(typeElement.asType(), tileEntityElement.asType())) {
                messager.printMessage(Diagnostic.Kind.ERROR, "Class annotated with @AutoRegisterTE must extend net.minecraft.tileentity.TileEntity" +
                        ".", element);
                continue;
            }
            try {
                String qualifiedName = typeElement.getQualifiedName().toString();
                String simpleName = typeElement.getSimpleName().toString();
                AutoRegister annotation = typeElement.getAnnotation(AutoRegister.class);
                String registrationId = annotation.value();
                if (registrationId.trim().isEmpty()) registrationId = generateRegistrationId(simpleName);
                tileEntitiesToRegister.put(qualifiedName, registrationId);
            } catch (Exception e) {
                messager.printMessage(Diagnostic.Kind.ERROR, "Could not process @AutoRegisterTE annotation: " + e.getMessage(), element);
            }
        }
        if (roundEnv.processingOver() && !tileEntitiesToRegister.isEmpty()) {
            try {
                generateRegistrarFile();
            } catch (IOException e) {
                messager.printMessage(Diagnostic.Kind.ERROR, "Failed to generate TileEntity registrar file: " + e.getMessage());
            }
        }
        return true;
    }

    private void generateRegistrarFile() throws IOException {
        //Configurables Generator
        ClassName listClass = ClassName.get("java.util", "ArrayList");
        ClassName hashMapClass = ClassName.get("java.util", "HashMap");
        ClassName stringClass = ClassName.get("java.lang", "String");
        ClassName classClass = ClassName.get("java.lang", "Class");
        ClassName iface = ClassName.get("com.hbm.tileentity", "IConfigurableMachine");
        ClassName tileEntity = ClassName.get("net.minecraft.tileentity", "TileEntity");
        TypeName wildcardConfig = WildcardTypeName.subtypeOf(iface);
        TypeName wildcardTE = WildcardTypeName.subtypeOf(tileEntity);
        TypeName classOfWildcardTE = ParameterizedTypeName.get(classClass, wildcardTE);
        TypeName classOfWildcardConfigurable = ParameterizedTypeName.get(classClass, wildcardConfig);
        TypeName arrayListOfClass = ParameterizedTypeName.get(listClass, classOfWildcardConfigurable);
        TypeName hashMapOfClass = ParameterizedTypeName.get(hashMapClass, classOfWildcardTE, stringClass);

        FieldSpec tileEntities = FieldSpec.builder(hashMapOfClass, "registryMap")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("new $T<>()", hashMapClass)
                .build();

        FieldSpec configurableList = FieldSpec.builder(arrayListOfClass, "configurable")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("new $T<>()", listClass)
                .build();


        TypeName clazzType = ParameterizedTypeName.get(classClass, WildcardTypeName.subtypeOf(tileEntity));

        ParameterSpec clazzParam = ParameterSpec.builder(clazzType, "clazz").build();
        ParameterSpec nameParam = ParameterSpec.builder(ClassName.get(String.class), "name").build();

        MethodSpec registerMethod = MethodSpec.methodBuilder("register")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(TypeName.VOID)
                .addParameter(clazzParam)
                .addParameter(nameParam)
                .addStatement("$N.put($N,$N)", "registryMap", "clazz", "name")
                .beginControlFlow("if ($T.class.isAssignableFrom($N))", iface, "clazz")
                .addStatement("$N.add(($T)$N)", "configurable", classOfWildcardConfigurable, "clazz")
                .endControlFlow()
                .build();


        MethodSpec.Builder registerAllMethod = MethodSpec.methodBuilder("registerAll")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addJavadoc("Generated by HBM's annotation processor. Do not edit this file manually!");
        for (Map.Entry<String, String> entry : tileEntitiesToRegister.entrySet()) {
            String classFqn = entry.getKey();
            String regId = entry.getValue();
            ClassName tileEntityClass = ClassName.bestGuess(classFqn);
            registerAllMethod.addStatement(
                    "register($T.class, $S)",
                    tileEntityClass, regId);
        }
        TypeSpec generatedRegistrar = TypeSpec.classBuilder("GeneratedTERegistrar")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addField(tileEntities)
                .addField(configurableList)
                .addMethod(registerAllMethod.build())
                .addMethod(registerMethod)
                .build();
        JavaFile javaFile = JavaFile.builder("com.hbm.generated", generatedRegistrar)
                .addFileComment("AUTO-GENERATED FILE. DO NOT MODIFY.")
                .build();
        javaFile.writeTo(filer);
    }

    private String generateRegistrationId(String name) {
        name = name.replaceFirst("^TileEntity", "");
        return "tileentity_" + name.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }
}
