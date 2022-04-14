package com.zg.database.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PassWordUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(PassWordUtil.class.getName());

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
        LOGGER.info("HvgaE#7ML_      " + encryptUtil.AESencode("HvgaE#7ML_", "hello"));
        LOGGER.info("Zst_phq123#      " + encryptUtil.AESencode("Zst_phq123#", "hello"));
        LOGGER.info("Zg_phq123#      " + encryptUtil.AESencode("Zg_phq123#", "hello"));
        LOGGER.info("helloworld      " + encryptUtil.AESencode("helloworld", "hello"));
        LOGGER.info("basecode      " + encryptUtil.AESencode("basecode", "hello"));
        LOGGER.info("stageapp1154      " + encryptUtil.AESencode("stageapp1154", "hello"));

        LOGGER.info("HvgaE#7ML_      " + encryptUtil.AESdecode("3CE6B72634FF4536552C0DD08D24DB1A", "hello"));
        LOGGER.info("Zst_phq123#      " + encryptUtil.AESdecode("1449820864936D6E26FAFF20D0DF8E00", "hello"));
        LOGGER.info("Zg_phq123#      " + encryptUtil.AESdecode("2F541CBF36E75CF263D15EE74CAF2BD6", "hello"));
    }
}
