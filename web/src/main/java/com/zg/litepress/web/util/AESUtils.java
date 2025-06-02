package com.zg.litepress.web.util;

import org.bouncycastle.util.encoders.Hex;

public class AESUtils {

    public static AES aes = new AES();


    public static String encode(String content, String key) {
        byte[] enc = aes.encrypt(content.getBytes(), key.getBytes());
        return new String(Hex.encode(enc));
    }

    public static String decode(String content, String key) {
        byte[] enc = aes.encrypt(content.getBytes(), key.getBytes());
        return new String(Hex.encode(enc));
    }


}
