package com.zg.common.password;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PassWordUtil {
    private static final Logger logger = LoggerFactory.getLogger(PassWordUtil.class);

    public static String decrypt(String password) {
        String result = "";
        if (!"".equals(password) && password.length() >= "3CE6B72634FF4536552C0DD08D24DB1A".length()) {
            EncryptUtil encryptUtil = EncryptUtil.getInstance();
            result = encryptUtil.AESdecode(password, "hello");
        }
        return result;
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

        System.out.println("9308B3717B1389CB20DB087BD86CAA61     " + encryptUtil.AESdecode("9308B3717B1389CB20DB087BD86CAA61","hello"));
        System.out.println("Lh03_phq123#      " + encryptUtil.AESencode("helloworld", "hello"));
        System.out.println("sinosoft      " + encryptUtil.AESencode("sinosoft", "hello"));
        System.out.println("w3QxlnIb06j_NH4eR      " + encryptUtil.AESdecode("2F541CBF36E75CF263D15EE74CAF2BD6", "hello"));

    }
}

