package com.zg.common.util.reflect;

import java.io.*;
import java.util.HashMap;

public class DynameicObjectOutputStream extends ObjectInputStream {
    private Class modelClass;
    public DynameicObjectOutputStream(InputStream in,Class modelClass) throws IOException {
        super(in);
        this.modelClass=modelClass;
    }


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
    }


    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        String name = desc.getName();
        try {
            ClassLoader loader=modelClass.getClassLoader();
            return Class.forName(name, false, loader);
        } catch (ClassNotFoundException ex) {
            Class cl = (Class) primClasses.get(name);
            if (cl != null) {
                return cl;
            } else {
                throw ex;
            }
        }
    }
}
