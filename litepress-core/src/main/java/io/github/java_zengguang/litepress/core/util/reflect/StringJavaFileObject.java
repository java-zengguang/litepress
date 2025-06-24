package io.github.java_zengguang.litepress.core.util.reflect;

import javax.tools.SimpleJavaFileObject;
import java.io.IOException;
import java.net.URI;

/**
 * Created by Administrator on 2018/11/28 0028.
 */

public class StringJavaFileObject extends SimpleJavaFileObject {
    private String content = null;

    protected StringJavaFileObject(String name, String javaCode) {
        super(StringJavaFileObject.createURI(name), Kind.SOURCE);
        content = javaCode;
    }

    private static URI createURI(String name) {
        return URI.create("String:///" + name + Kind.SOURCE.extension);
    }

    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        return content;
    }
}

