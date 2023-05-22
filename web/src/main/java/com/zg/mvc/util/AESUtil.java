package com.zg.mvc.util;


import java.io.UnsupportedEncodingException;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

public class AESUtil {
    private static volatile SecretKeySpec keySpec;
    private static volatile IvParameterSpec ivSpec;
    private static final int KEY_SIZE = 16;
    private static volatile byte[] keyBytes;
    public static String ECARGO_KEY = "sinotrans";
    private static final String IV_STRING = "16-Bytes--String";

    public AESUtil() {
    }

    public static synchronized String decodeData(String signData, String key) {
        try {
            init(key);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(2, keySpec, ivSpec);
            byte[] results = cipher.doFinal(Base64.decodeBase64(signData.getBytes()));
            return new String(results, "GBK");
        } catch (Exception var4) {
            var4.printStackTrace();
            return "error";
        }
    }

    public static synchronized String encodeData(String data, String key) throws Exception {
        String signData = "";
        init(key);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(1, keySpec, ivSpec);
        byte[] results = cipher.doFinal(data.getBytes("GBK"));
        signData = new String(Base64.encodeBase64(results), "GBK");
        return signData;
    }

    private static synchronized void init(String key) throws Exception {
        keyBytes = new byte[16];

        try {
            byte[] b = key.getBytes("GBK");
            int len = b.length;
            if (len > keyBytes.length) {
                len = keyBytes.length;
            }

            System.arraycopy(b, 0, keyBytes, 0, len);
        } catch (UnsupportedEncodingException var3) {
            throw new Exception(var3.getMessage());
        }

        keySpec = new SecretKeySpec(keyBytes, "AES");
        ivSpec = new IvParameterSpec(keyBytes);
    }

    public static String encryptData(String key, String content) {
        byte[] encryptedBytes = new byte[0];

        try {
            byte[] byteContent = content.getBytes("UTF-8");
            byte[] enCodeFormat = key.getBytes();
            SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, "AES");
            byte[] initParam = "16-Bytes--String".getBytes();
            IvParameterSpec ivParameterSpec = new IvParameterSpec(initParam);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(1, secretKeySpec, ivParameterSpec);
            encryptedBytes = cipher.doFinal(byteContent);
            return new String(Base64.encodeBase64(encryptedBytes), "UTF-8");
        } catch (Exception var9) {
            var9.printStackTrace();
            return null;
        }
    }

    public static String decryptData(String key, String content) {
        try {
            byte[] encryptedBytes = Base64.decodeBase64(content.getBytes("UTF-8"));
            byte[] enCodeFormat = key.getBytes();
            SecretKeySpec secretKey = new SecretKeySpec(enCodeFormat, "AES");
            byte[] initParam = "16-Bytes--String".getBytes();
            IvParameterSpec ivParameterSpec = new IvParameterSpec(initParam);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(2, secretKey, ivParameterSpec);
            byte[] result = cipher.doFinal(encryptedBytes);
            return new String(result, "UTF-8");
        } catch (Exception var9) {
            var9.printStackTrace();
            return null;
        }
    }
}
