package com.zg.database.util;

public class PassWordUtil {
    public static String decrypt(String password) {
        String result = "";
        if (!"".equals(password) && password.length() == "3CE6B72634FF4536552C0DD08D24DB1A".length()) {
            EncryptUtil encryptUtil = EncryptUtil.getInstance();
            result = encryptUtil.AESdecode(password, "hello");
        }
        return result;
    }


    public static void main(String args[]) {
        EncryptUtil encryptUtil = EncryptUtil.getInstance();
        System.out.println("HvgaE#7ML_      " + encryptUtil.AESencode("HvgaE#7ML_", "hello"));
        System.out.println("Zst_phq123#      " + encryptUtil.AESencode("Zst_phq123#", "hello"));
        System.out.println("Zg_phq123#      " + encryptUtil.AESencode("Zg_phq123#", "hello"));
        System.out.println("helloworld      " + encryptUtil.AESencode("helloworld", "hello"));
        System.out.println("basecode      " + encryptUtil.AESencode("basecode", "hello"));
        System.out.println("stageapp1154      " + encryptUtil.AESencode("stageapp1154", "hello"));

        System.out.println("HvgaE#7ML_      " + encryptUtil.AESdecode("3CE6B72634FF4536552C0DD08D24DB1A", "hello"));
        System.out.println("Zst_phq123#      " + encryptUtil.AESdecode("1449820864936D6E26FAFF20D0DF8E00", "hello"));
        System.out.println("Zg_phq123#      " + encryptUtil.AESdecode("2F541CBF36E75CF263D15EE74CAF2BD6", "hello"));
    }
}
