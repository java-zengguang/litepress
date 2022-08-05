package com.zg.common.util.reflect;

import com.zg.common.Test2;
import com.zg.common.bean.entity.MetadataEntity;
import com.zg.common.util.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.*;
import javax.tools.JavaCompiler.CompilationTask;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;


public class DynamicClass {
    private static final Logger logger = LoggerFactory.getLogger(DynamicClass.class.getName());


    private static String produceEntityJavaCode(List<String> referenceList, String calssName, Map<String, String> natureMap, List<String> interfaceList, String parentClass) throws Exception {

        StringBuffer sb = new StringBuffer();
        if (calssName == null || natureMap.size() == 0) {
            throw new Exception();
        } else {
            sb.append("package com.zg.common.bean.entity;\r\n");
            if (referenceList != null && referenceList.size() > 0) {
                for (String reference : referenceList) {
                    sb.append("import " + reference + ";\r\n");
                }
            }
            sb.append("import java.util.*;\r\n");
            sb.append("@Model(tableName = \"" + calssName.toUpperCase() + "\")\n");
            sb.append("@FieldTypeMode(typeMode = \"entity\")\n");
            sb.append("public class ");
            sb.append(calssName);
            if (parentClass != null)
                sb.append(" extends " + parentClass);
            if (interfaceList != null && interfaceList.size() > 0) {
                sb.append(" implements ");
                for (String inter : interfaceList) {
                    sb.append(inter + " ");
                }
            }
            sb.append("{\r\n");
            Set<String> natureSet = natureMap.keySet();
            for (String name : natureSet) {
                sb.append("public " + natureMap.get(name) + " " + name + ";\r\n");
            }
            sb.append("}");
        }

        return sb.toString();
    }

    public static Class getDynamicModel(List<MetadataEntity> columnList) {
        Class model=null;
        if(columnList!=null&&columnList.size()>0) {
            String entityName="";
            List importList = Arrays.asList("com.zg.common.bean.entity.MainModel",
                    "com.zg.common.annotation.FieldTypeMode",
                    "com.zg.common.annotation.Model",
                    "java.math.BigDecimal");
            Map tableInfoMap=new HashMap();
            for(MetadataEntity metadataEntity:columnList){
                entityName=metadataEntity.entityName;
               tableInfoMap.put(metadataEntity.fieldName,metadataEntity.fieldType);
            }
            model = DynamicClass.getDynamicModel(importList, entityName, tableInfoMap, null, "MainModel");
        }
        return model;
    }

    public static Class getDynamicModel(List<String> referenceList, String className, Map<String, String> natureMap, List<String> interfaceList, String parentClass) {
        String javaCode;
        try {
            javaCode = produceEntityJavaCode(referenceList, className, natureMap, interfaceList, parentClass);
            return getDynamicModel(className, javaCode);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

    }
    private static Class getDynamicModel(String name, String javaCode) throws ClassNotFoundException, InstantiationException, IllegalAccessException, FileNotFoundException, MalformedURLException, URISyntaxException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager standardFileManager = compiler.getStandardFileManager(null, null, null);
        ClassJavaFileManager classJavaFileManager = new ClassJavaFileManager(standardFileManager);
        StringObject stringObject = new StringObject(new URI(name+".java"), JavaFileObject.Kind.SOURCE, javaCode);
        List<String> options = new ArrayList<String>();
        String path = CommonUtil.getThisPath()+"";
        logger.info(DynamicClass.class + "====calss生成路径" + path);
        options.addAll(Arrays.asList("-d",path,"--limit-modules","java.base,java.logging"));
        JavaCompiler.CompilationTask task = compiler.getTask(null, classJavaFileManager, null, options, null, Arrays.asList(stringObject));
        if (task.call()) {
            ClassJavaFileObject javaFileObject = classJavaFileManager.getClassJavaFileObject();
            ClassLoader classLoader = new MyClassLoader(javaFileObject);
            String classAllName="com.zg.common.bean.entity." + name;
            Class clazz = classLoader.loadClass(classAllName);
            return clazz;

        }
        return null;
    }

    static class ClassJavaFileManager extends ForwardingJavaFileManager {

        private ClassJavaFileObject classJavaFileObject;
        public ClassJavaFileManager(JavaFileManager fileManager) {
            super(fileManager);
        }

        public ClassJavaFileObject getClassJavaFileObject() {
            return classJavaFileObject;
        }
        //这个方法一定要自定义
        @Override
        public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
            return (classJavaFileObject = new ClassJavaFileObject(className,kind));
        }
    }
    /**
     * 存储源文件
     */
    static class StringObject extends SimpleJavaFileObject{

        private String content;

        public StringObject(URI uri, Kind kind, String content) {
            super(uri, kind);
            this.content = content;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
            return this.content;
        }
    }

    /**
     * class文件（不需要存到文件中）
     */
    static class ClassJavaFileObject extends SimpleJavaFileObject{

        ByteArrayOutputStream outputStream;

        public ClassJavaFileObject(String className, Kind kind) {
            super(URI.create(className + kind.extension), kind);
            this.outputStream = new ByteArrayOutputStream();
        }
        //这个也要实现
        @Override
        public OutputStream openOutputStream() throws IOException {
            return this.outputStream;
        }

        public byte[] getBytes(){
            return this.outputStream.toByteArray();
        }
    }
    //自定义classloader
    static class MyClassLoader extends ClassLoader{
        private ClassJavaFileObject stringObject;
        public MyClassLoader(ClassJavaFileObject stringObject){
            this.stringObject = stringObject;
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            byte[] bytes = this.stringObject.getBytes();
            return defineClass(name,bytes,0,bytes.length);
        }
    }



/*

    private static Class getDynamicModel(String name, String javaCode) throws ClassNotFoundException, InstantiationException, IllegalAccessException, FileNotFoundException, MalformedURLException {
        Object o = null;
        Map<String, byte[]> results;
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        JavaFileManager stdManager = compiler.getStandardFileManager(null, null, null);
        JavaFileObject jfo = new StringJavaFileObject(name, javaCode);
        List<String> options = new ArrayList<String>();
        String path = CommonUtil.PATH+"";
        System.out.println(DynamicClass.class + "====calss生成路径" + path);
        options.addAll(Arrays.asList("-d",path,"--limit-modules","java.base,java.logging"));
        List<? extends JavaFileObject> jfos = Arrays.asList(jfo);

        CompilationTask task = compiler.getTask(null, stdManager, null, options, null, jfos);
        if (task.call()) {
            String classAllName="com.zg.common.bean.entity." + name;
            DynameicClassLoader dynameicClassLoader=new DynameicClassLoader(path);
            Class classes= dynameicClassLoader.loadClass(classAllName);
            return classes;
        } else {
            return null;
        }
    }
*/



}

