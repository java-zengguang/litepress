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
        logger.info("V23##*#QxGDkcmUJ      " + encryptUtil.AESencode("V23##*#QxGDkcmUJ", "hello"));
        logger.info("Zst_phq123#      " + encryptUtil.AESencode("Zst_phq123#", "hello"));
        logger.info("Zg_phq123#      " + encryptUtil.AESencode("Zg_phq123#", "hello"));
        logger.info("helloworld      " + encryptUtil.AESencode("helloworld", "hello"));
        logger.info("basecode      " + encryptUtil.AESencode("basecode", "hello"));
        logger.info("QAerUDN#VlrQ1pX0_      " + encryptUtil.AESencode("QAerUDN#VlrQ1pX0_", "hello"));

        logger.info("HvgaE#7ML_      " + encryptUtil.AESdecode("3CE6B72634FF4536552C0DD08D24DB1A", "hello"));
        logger.info("Zst_phq123#      " + encryptUtil.AESdecode("1449820864936D6E26FAFF20D0DF8E00", "hello"));
        logger.info("Zg_phq123#      " + encryptUtil.AESdecode("2F541CBF36E75CF263D15EE74CAF2BD6", "hello"));

        System.out.println("5E9174B14F22058515F85A5E074F478FA149B02E45F7C9C8504897D34C9DC75B     " + encryptUtil.AESdecode("5E9174B14F22058515F85A5E074F478FA149B02E45F7C9C8504897D34C9DC75B","hello"));
        System.out.println("3GaHIKC_U5ym%6nxkD      " + encryptUtil.AESencode("3GaHIKC_U5ym%6nxkD", "hello"));
        System.out.println("oR2kjDx0QfRx      " + encryptUtil.AESencode("oR2kjDx0QfRx", "hello"));
        System.out.println("w3QxlnIb06j_NH4eR      " + encryptUtil.AESdecode("2F541CBF36E75CF263D15EE74CAF2BD6", "hello"));

        System.out.println("6E9D7C22309AC2E9E5F49CAEB104201A4975CC2A4A8DDCB847D483709160BD9B    " + encryptUtil.AESdecode("6E9D7C22309AC2E9E5F49CAEB104201A4975CC2A4A8DDCB847D483709160BD9B", "hello"));

    }
}

