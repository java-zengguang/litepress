package com.zg.mvc.util;

import org.bouncycastle.util.encoders.Hex;

public class AESUtils {

    public static AES aes = new AES();


    public static String encode(String content, String key) {
        byte[] enc = aes.encrypt(content.getBytes(), key.getBytes());
        String encode = new String(Hex.encode(enc));
        return encode;
    }

    public static String decode(String content, String key) {
        byte[] enc = aes.encrypt(content.getBytes(), key.getBytes());
        String decode=new String(Hex.encode(enc));
        return decode;
    }


}
