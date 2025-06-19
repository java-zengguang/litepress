package com.zg.mvc.util;

import jakarta.servlet.http.HttpServletRequest;

public class ServletUtils {

    public static String getRemoteAdd(HttpServletRequest request){
        String[] headerNames = {"X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_X_FORWARDED_FOR", "HTTP_X_FORWARDED", "HTTP_X_CLUSTER_CLIENT_IP", "HTTP_CLIENT_IP", "HTTP_FORWARDED_FOR", "HTTP_FORWARDED", "HTTP_VIA", "REMOTE_ADDR"};
        String clientIp = null;

        for (String header : headerNames) {
            String ip = request.getHeader(header);
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                clientIp = ip;
                break;
            }
        }

        if (clientIp == null) {
            clientIp = request.getRemoteAddr();
        }

        return clientIp;
    }
}
