package com.zg.common.util.reflect;

import org.tinylog.Logger;

import java.nio.file.Files;
import java.nio.file.Paths;

public class DynameicClassLoader extends ClassLoader {

    public static final byte DIGITAL_255 = (byte) 255;
    private String rootPath;

    public DynameicClassLoader(String rootPath) {
        this.rootPath = rootPath;
    }

    //重写该方法
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class classes = null;
        try {

            byte[] bytes = getClassBytes(name);

            // 使用新的字节数组定义类

            classes = defineClass(name, bytes, 0, bytes.length);
        } catch (Exception e) {
            Logger.error(e);
        }
        if (classes == null) {
            super.findClass(name);
        }

        return classes;
    }


    private byte[] getClassBytes(String classAllName) throws Exception {
        String classPath = rootPath + classAllName.replaceAll("\\.", "\\\\") + ".class";
        Logger.debug("自定义类加载====" + classPath);
        return Files.readAllBytes(Paths.get(classPath));
    }
}


