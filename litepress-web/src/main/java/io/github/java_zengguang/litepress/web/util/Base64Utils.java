package io.github.java_zengguang.litepress.web.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @author tongxiangbo
 * @description base64 工具类
 * @Date 2021/1/26
 **/
public class Base64Utils {
    /**
     * 加密JDK1.8
     */
    public static String encode(String str) {
        byte[] encodeBytes = Base64.getEncoder().encode(str.getBytes(StandardCharsets.UTF_8));
        return new String(encodeBytes);
    }

    /**
     * 加密JDK1.8
     */
    public static String encode(byte[] byteArray) {
        byte[] encodeBytes = Base64.getEncoder().encode(byteArray);
        return new String(encodeBytes);
    }

    /**
     * 解密JDK1.8
     */
    public static String decode(String str) {
        byte[] decodeBytes = Base64.getDecoder().decode(str.getBytes(StandardCharsets.UTF_8));
        return new String(decodeBytes);
    }

    /**
     * 加密JDK1.8
     */
    public static String encodeThrowsException(String str) throws UnsupportedEncodingException {
        byte[] encodeBytes = Base64.getEncoder().encode(str.getBytes(StandardCharsets.UTF_8));
        return new String(encodeBytes);
    }

    /**
     * 解密JDK1.8
     */
    public static String decodeThrowsException(String str) throws UnsupportedEncodingException {
        byte[] decodeBytes = Base64.getDecoder().decode(str.getBytes(StandardCharsets.UTF_8));
        return new String(decodeBytes);
    }

}
