package io.github.java_zengguang.litepress.core.bean.factory;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.tinylog.Logger;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * BeanConfig配置文件验证器。
 * 检查XML中bean的class是否存在、property的name是否与Java类字段匹配、type标注是否与字段类型一致。
 */
public class BeanConfigValidator {

    public static class ConfigError {
        public final String file;
        public final String beanId;
        public final String beanClass;
        public final String property;
        public final String xmlType;
        public final String fieldType;
        public final String message;
        public final boolean critical;

        public ConfigError(String file, String beanId, String beanClass, String property,
                           String xmlType, String fieldType, String message, boolean critical) {
            this.file = file;
            this.beanId = beanId;
            this.beanClass = beanClass;
            this.property = property;
            this.xmlType = xmlType;
            this.fieldType = fieldType;
            this.message = message;
            this.critical = critical;
        }

        @Override
        public String toString() {
            String level = critical ? "ERROR" : "WARN";
            if (property != null) {
                return "[%s] %s → bean[%s] %s.%s | %s (xmlType=%s, fieldType=%s)"
                        .formatted(level, file, beanId, beanClass, property, message, xmlType, fieldType);
            }
            return "[%s] %s → bean[%s] class=%s | %s"
                    .formatted(level, file, beanId, beanClass, message);
        }
    }

    public static List<ConfigError> validate(String fileName, String xmlContent) {
        List<ConfigError> errors = new ArrayList<>();
        try (InputStream is = new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8))) {
            SAXReader reader = new SAXReader();
            Document document = reader.read(is);
            Element root = document.getRootElement();

            for (Object beanO : root.elements("bean")) {
                Element bean = (Element) beanO;
                String beanId = bean.attributeValue("id");
                String beanClassName = bean.attributeValue("class");

                Class<?> clazz;
                try {
                    clazz = Class.forName(beanClassName);
                } catch (ClassNotFoundException e) {
                    errors.add(new ConfigError(fileName, beanId, beanClassName,
                            null, null, null, "类不存在", true));
                    continue;
                }

                for (Object propO : bean.elements("property")) {
                    Element prop = (Element) propO;
                    String propName = prop.attributeValue("name");
                    String xmlType = prop.attributeValue("type");

                    if (prop.attributeValue("ref") != null) {
                        continue;
                    }

                    Field field;
                    try {
                        field = clazz.getField(propName);
                    } catch (NoSuchFieldException e) {
                        errors.add(new ConfigError(fileName, beanId, beanClassName,
                                propName, null, null, "字段不存在", true));
                        continue;
                    }

                    if (xmlType != null) {
                        String normalizedXmlType = xmlType.replace("java.lang.", "");
                        String javaTypeName = field.getType().getSimpleName();
                        if (!typeCompatible(normalizedXmlType, javaTypeName)) {
                            errors.add(new ConfigError(fileName, beanId, beanClassName,
                                    propName, normalizedXmlType, javaTypeName, "类型标注不匹配", false));
                        }
                    }
                }
            }
        } catch (Exception e) {
            errors.add(new ConfigError(fileName, null, null,
                    null, null, null, "XML解析失败: " + e.getMessage(), true));
        }
        return errors;
    }

    private static boolean typeCompatible(String xmlType, String javaType) {
        if (xmlType.equals(javaType)) return true;
        if (xmlType.equals("int") && javaType.equals("Integer")) return true;
        if (xmlType.equals("long") && javaType.equals("Long")) return true;
        if (xmlType.equals("boolean") && javaType.equals("Boolean")) return true;
        if (xmlType.equals("double") && javaType.equals("Double")) return true;
        if (xmlType.equals("float") && javaType.equals("Float")) return true;
        if (xmlType.equals("Integer") && javaType.equals("int")) return true;
        if (xmlType.equals("Long") && javaType.equals("long")) return true;
        return false;
    }

    public static List<ConfigError> validateEnvironment(String environment) {
        List<ConfigError> allErrors = new ArrayList<>();
        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        String base = readFile(cl, "BeanConfig.xml");
        if (base != null) {
            allErrors.addAll(validate("BeanConfig.xml", base));
        }

        String env = readFile(cl, environment + "_BeanConfig.xml");
        if (env != null) {
            allErrors.addAll(validate(environment + "_BeanConfig.xml", env));
        }

        return allErrors;
    }

    private static String readFile(ClassLoader cl, String name) {
        try (InputStream is = cl.getResourceAsStream(name)) {
            if (is == null) {
                Logger.info("文件未找到: {}", name);
                return null;
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            Logger.warn(e, "读取失败: {}", name);
            return null;
        }
    }

    public static void main(String[] args) {
        String env = args.length > 0 ? args[0] : System.getenv().getOrDefault("TargetEvn", "dev");
        System.out.println("验证环境: " + env);
        System.out.println("────────────────────────────────────────");

        List<ConfigError> errors = validateEnvironment(env);

        if (errors.isEmpty()) {
            System.out.println("配置文件验证通过，无问题。");
        } else {
            long errorsCount = errors.stream().filter(e -> e.critical).count();
            long warnCount = errors.stream().filter(e -> !e.critical).count();
            System.out.println("发现 %d 个问题 (ERROR=%d, WARN=%d):".formatted(errors.size(), errorsCount, warnCount));
            System.out.println();
            errors.forEach(System.out::println);
        }
    }
}
