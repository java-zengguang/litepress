package com.zg.common.relect.bytebuddy;

import com.zg.common.annotation.*;
import com.zg.common.bean.entity.MainModel;
import com.zg.common.bean.entity.MetadataEntity;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.utility.JavaModule;
import org.tinylog.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class DynamicClassGenerator {
    private static final HashMap<String, Class<?>> primClasses
            = new HashMap<>(8, 1.0F);

    static {
        primClasses.put("boolean", boolean.class);
        primClasses.put("byte", byte.class);
        primClasses.put("char", char.class);
        primClasses.put("short", short.class);
        primClasses.put("int", int.class);
        primClasses.put("long", long.class);
        primClasses.put("float", float.class);
        primClasses.put("double", double.class);
        primClasses.put("void", void.class);
        primClasses.put("String", String.class);
        primClasses.put("Integer", Integer.class);
        primClasses.put("BigDecimal", BigDecimal.class);
        primClasses.put("Double", Double.class);
        primClasses.put("Date", Date.class);
    }


    public static Class string2Class(String classType) throws ClassNotFoundException {
        if (primClasses.containsKey(classType)) {
            return primClasses.get(classType);
        } else {
            return Class.forName(classType);
        }
    }

    public static Class getDynamicModel(List<MetadataEntity> columnList) {
        Logger.info("动态类生成开始");
        Class clazz = null;
        try {
            // 使用 ByteBuddy 动态生成类
            DynamicType.Builder byteBuddyBuilder = new ByteBuddy()
                    .subclass(MainModel.class)
                    .name("com.zg.common.bean.entity." + columnList.get(0).entityName)
                    .annotateType(
                            AnnotationDescription.Builder.ofType(Model.class).define("tableName", columnList.get(0).tableName).build(),
                            AnnotationDescription.Builder.ofType(FieldTypeMode.class).define("typeMode", "entity").build()
                    );
            Logger.info( "动态实体加载-1");

            for (MetadataEntity metadataEntity : columnList) {

                List<Class<? extends Annotation>> annotations = new ArrayList<>();
                if ("1".equals(metadataEntity.isPK)) {
                    annotations.add(PrimaryKey.class);
                }
                if ("1".equals(metadataEntity.isNotCommit)) {
                    annotations.add(NotCommitField.class);
                }
                if ("1".equals(metadataEntity.isAutoIncrease)) {
                    annotations.add(AutoIncrease.class);
                }
                Collection<AnnotationDescription> annotationDescriptionList = annotations.stream().map((annotation) -> AnnotationDescription.Builder.ofType(annotation).build()).collect(Collectors.toSet());
                if (annotationDescriptionList.size() > 0) {
                    byteBuddyBuilder = byteBuddyBuilder
                            .defineField(metadataEntity.fieldName, string2Class(metadataEntity.fieldType), Modifier.PUBLIC)
                            .annotateField(annotationDescriptionList);
                } else {
                    byteBuddyBuilder = byteBuddyBuilder
                            .defineField(metadataEntity.fieldName, string2Class(metadataEntity.fieldType), Modifier.PUBLIC);
                }

            }
            Logger.info( "动态实体加载-2");
            clazz = byteBuddyBuilder.make()
                    .load(DynamicClassGenerator.class.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                    .getLoaded();
            Logger.info( "动态实体加载完成");

        } catch (Exception e) {
            Logger.error(e, "动态实体加载失败");
        }
        return clazz;
    }


}
