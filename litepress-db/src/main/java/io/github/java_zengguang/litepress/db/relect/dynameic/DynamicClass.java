package io.github.java_zengguang.litepress.db.relect.dynameic;



import io.github.java_zengguang.litepress.db.po.EntityDataPo;
import io.github.java_zengguang.litepress.db.po.MetaDataPo;
import io.github.java_zengguang.litepress.db.relect.bytebuddy.DynamicClassGenerator;
import io.github.java_zengguang.litepress.core.util.CommonUtil;
import org.tinylog.Logger;

import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class DynamicClass {


    public static Class getDynamicModel(EntityDataPo entityDataPo) {


        return DynamicClassGenerator.getDynamicModel(entityDataPo);
    }



    private static Class getDynamicModel(String name, String javaCode) throws ClassNotFoundException, InstantiationException, IllegalAccessException, FileNotFoundException, MalformedURLException, URISyntaxException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager standardFileManager = compiler.getStandardFileManager(null, null, null);
        ClassJavaFileManager classJavaFileManager = new ClassJavaFileManager(standardFileManager);
        StringObject stringObject = new StringObject(new URI(name + ".java"), JavaFileObject.Kind.SOURCE, javaCode);
        List<String> options = new ArrayList<String>();
        String path = CommonUtil.getThisPath(DynamicClass.class);
        Logger.info(DynamicClass.class + "====calss生成路径" + path);
        options.addAll(Arrays.asList("-d", path,"--module-path",path, "--limit-modules", "java.base,java.logging"));
        JavaCompiler.CompilationTask task = compiler.getTask(null, classJavaFileManager, null, options, null, Arrays.asList(stringObject));
        if (task.call()) {
            ClassJavaFileObject javaFileObject = classJavaFileManager.getClassJavaFileObject();
            ClassLoader classLoader = new MyClassLoader(javaFileObject);
            String classAllName = "com.zg.litepress.core.bean.entity." + name;
            return classLoader.loadClass(classAllName);

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
            return (classJavaFileObject = new ClassJavaFileObject(className, kind));
        }
    }

    /**
     * 存储源文件
     */
    static class StringObject extends SimpleJavaFileObject {

        private final String content;

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
    static class ClassJavaFileObject extends SimpleJavaFileObject {

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

        public byte[] getBytes() {
            return this.outputStream.toByteArray();
        }
    }

    //自定义classloader
    static class MyClassLoader extends ClassLoader {
        private final ClassJavaFileObject stringObject;

        public MyClassLoader(ClassJavaFileObject stringObject) {
            this.stringObject = stringObject;
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            byte[] bytes = this.stringObject.getBytes();
            return defineClass(name, bytes, 0, bytes.length);
        }
    }




}

