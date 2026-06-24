package io.github.java_zengguang.litepress.core.util.url;

import org.tinylog.Logger;

import java.nio.charset.StandardCharsets;

public class URLUtils {
    public static String getURLEncoderString(String str) {
        String result = "";
        if (null == str) {
            return "";
        }
        result = java.net.URLEncoder.encode(str, StandardCharsets.UTF_8);
        return result;
    }

    public static String URLDecoderString(String str) {
        String result = "";
        if (null == str) {
            return "";
        }
        result = java.net.URLDecoder.decode(str, StandardCharsets.UTF_8);
        return result;
    }
}
