//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.zg.common.util.reflect;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.ObjectMap;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;

public class DynameicSerializer extends Serializer {
    public static Class clazz;

    public DynameicSerializer(Class clazz) {
        this.clazz = clazz;
    }

    public void write(Kryo kryo, Output output, Object object) {
        try {
            ObjectMap graphContext = kryo.getGraphContext();
            ObjectOutputStream objectStream = (ObjectOutputStream)graphContext.get(this);
            if (objectStream == null) {
                objectStream = new ObjectOutputStream(output);
                graphContext.put(this, objectStream);
            }

            objectStream.writeObject(object);
            objectStream.flush();
        } catch (Exception var6) {
            throw new KryoException("Error during Java serialization.", var6);
        }
    }

    public Object read(Kryo kryo, Input input, Class type) {
        try {
            ObjectMap graphContext = kryo.getGraphContext();
            ObjectInputStream objectStream = (ObjectInputStream)graphContext.get(this);
            if (objectStream == null) {
                objectStream = new ObjectInputStreamWithKryoClassLoader(input, kryo);
                graphContext.put(this, objectStream);
            }

            return ((ObjectInputStream)objectStream).readObject();
        } catch (Exception var6) {
            throw new KryoException("Error during Java deserialization.", var6);
        }
    }

    private static class ObjectInputStreamWithKryoClassLoader extends ObjectInputStream {
        private final Kryo kryo;

        ObjectInputStreamWithKryoClassLoader(InputStream in, Kryo kryo) throws IOException {
            super(in);
            this.kryo = kryo;
        }

        protected Class resolveClass(ObjectStreamClass type) {
            try {
                return Class.forName(type.getName(), false, clazz.getClassLoader());
            } catch (ClassNotFoundException var3) {
                throw new KryoException("Class not found: " + type.getName(), var3);
            }
        }
    }
}
