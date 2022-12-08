package com.zg.common.password;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PassWordUtil {
    private static final Logger logger = LoggerFactory.getLogger(PassWordUtil.class);

    public static String decrypt(String password) {

        if (!"".equals(password) && password.length() >= "3CE6B72634FF4536552C0DD08D24DB1A".length()) {
            EncryptUtil encryptUtil = EncryptUtil.getInstance();
            password = encryptUtil.AESdecode(password, "hello");
        }
        return password;
    }


    public static void main(String args[]) {
        EncryptUtil encryptUtil = EncryptUtil.getInstance();
        logger.info("HvgaE#7ML_      " + encryptUtil.AESencode("HvgaE#7ML_", "hello"));
        logger.info("Zst_phq123#      " + encryptUtil.AESencode("Zst_phq123#", "hello"));
        logger.info("Zg_phq123#      " + encryptUtil.AESencode("Zg_phq123#", "hello"));
        logger.info("helloworld      " + encryptUtil.AESencode("helloworld", "hello"));
        logger.info("basecode      " + encryptUtil.AESencode("basecode", "hello"));
        logger.info("stageapp1154      " + encryptUtil.AESencode("stageapp1154", "hello"));

        logger.info("HvgaE#7ML_      " + encryptUtil.AESdecode("3CE6B72634FF4536552C0DD08D24DB1A", "hello"));
        logger.info("Zst_phq123#      " + encryptUtil.AESdecode("1449820864936D6E26FAFF20D0DF8E00", "hello"));
        logger.info("Zg_phq123#      " + encryptUtil.AESdecode("2F541CBF36E75CF263D15EE74CAF2BD6", "hello"));

        System.out.println("5E9174B14F22058515F85A5E074F478FA149B02E45F7C9C8504897D34C9DC75B     " + encryptUtil.AESdecode("5E9174B14F22058515F85A5E074F478FA149B02E45F7C9C8504897D34C9DC75B","hello"));
        System.out.println("3GaHIKC_U5ym%6nxkD      " + encryptUtil.AESencode("3GaHIKC_U5ym%6nxkD", "hello"));
        System.out.println("oR2kjDx0QfRx      " + encryptUtil.AESencode("oR2kjDx0QfRx", "hello"));
        System.out.println("w3QxlnIb06j_NH4eR      " + encryptUtil.AESdecode("2F541CBF36E75CF263D15EE74CAF2BD6", "hello"));

        System.out.println("9308B3717B1389CB20DB087BD86CAA61    " + encryptUtil.AESdecode("9308B3717B1389CB20DB087BD86CAA61", "hello"));

    }
}

