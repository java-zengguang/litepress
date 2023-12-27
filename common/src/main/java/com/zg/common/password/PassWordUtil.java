package com.zg.common.password;

import org.tinylog.Logger;


public class PassWordUtil {
    public static String decrypt(String password) {

        if (!"".equals(password) && password.length() >= "3CE6B72634FF4536552C0DD08D24DB1A".length()) {
            EncryptUtil encryptUtil = EncryptUtil.getInstance();
            password = encryptUtil.AESdecode(password, "hello");
        }
        return password;
    }


    public static void main(String[] args) {
        EncryptUtil encryptUtil = EncryptUtil.getInstance();


        Logger.info(" V23##*#QxGDkcmUJ      " + encryptUtil.AESencode("V23##*#QxGDkcmUJ", "hello"));
        Logger.info("Zst_phq123#      " + encryptUtil.AESencode("Zst_phq123#", "hello"));
        Logger.info("Zg_phq123#      " + encryptUtil.AESencode("Zg_phq123#", "hello"));
        Logger.info("Cvb_rTJRB7!w0oWV      " + encryptUtil.AESencode("Cvb_rTJRB7!w0oWV", "hello"));
        Logger.info("iDZT_7td      " + encryptUtil.AESencode("iDZT_7td", "hello"));
        Logger.info("QAerUDN#VlrQ1pX0_      " + encryptUtil.AESencode("QAerUDN#VlrQ1pX0_", "hello"));

        Logger.info("HvgaE#7ML_      " + encryptUtil.AESdecode("3CE6B72634FF4536552C0DD08D24DB1A", "hello"));
        Logger.info("Zst_phq123#      " + encryptUtil.AESdecode("1449820864936D6E26FAFF20D0DF8E00", "hello"));
        Logger.info("Zg_phq123#      " + encryptUtil.AESdecode("2F541CBF36E75CF263D15EE74CAF2BD6", "hello"));

        Logger.info("7D888B9E4F93023EA91FAB12DFB5DBAD4975CC2A4A8DDCB847D483709160BD9B     " + encryptUtil.AESdecode("7D888B9E4F93023EA91FAB12DFB5DBAD4975CC2A4A8DDCB847D483709160BD9B", "hello"));
        Logger.info("3GaHIKC_U5ym%6nxkD      " + encryptUtil.AESencode("3GaHIKC_U5ym%6nxkD", "hello"));
        Logger.info("oR2kjDx0QfRx      " + encryptUtil.AESencode("oR2kjDx0QfRx", "hello"));
        Logger.info("w3QxlnIb06j_NH4eR      " + encryptUtil.AESdecode("2F541CBF36E75CF263D15EE74CAF2BD6", "hello"));

        Logger.info("3CE6B72634FF4536552C0DD08D24DB1A    " + encryptUtil.AESdecode("3CE6B72634FF4536552C0DD08D24DB1A", "hello"));

    }
}

