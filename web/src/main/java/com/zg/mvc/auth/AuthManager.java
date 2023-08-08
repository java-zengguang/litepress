package com.zg.mvc.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.zg.common.init.Config;
import com.zg.mvc.auth.entity.AuthEntity;
import com.zg.mvc.entity.AuthConfig;
import com.zg.mvc.util.Base64Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.tinylog.Logger;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class AuthManager {

    private static AuthConfig authConfig = (AuthConfig) Config.getConfig("AuthConfig");
    private static List<String> whiteList = Arrays.asList("/Login/toLogin.do", "/favicon.ico");

    public boolean isWhite(HttpServletRequest request) {
        String pathInfo = request.getServletPath();
        Logger.info("访问路径：" + pathInfo);
        if (whiteList.contains(pathInfo)) {
            return true;
        }
        return false;
    }

    /**
     * 返回userInfo信息，验证失败返回null
     *
     * @param token Token
     * @return boolean 是否正确
     */
    public  Map verify(String token) {
        try {
            // 帐号加JWT私钥解密
            DecodedJWT jwt = JWT.decode(token);
            String safeToken = jwt.getClaim("safeToken").asString();
            String secret = safeToken + Base64Utils.decode(authConfig.perturbation);//token加私钥为密钥解密
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT result = verifier.verify(token);
            Map map = result.getClaim("userInfo").asMap();
            return map;
        } catch (IllegalArgumentException e) {
            Logger.error("JWTToken认证解密IllegalArgumentException异常:" + e.getMessage());
            return null;
        }

    }


    public String renewalToken(String token) {
        try {
            // 帐号加JWT私钥解密
            DecodedJWT jwt = JWT.decode(token);
            long expireTime = jwt.getExpiresAt().getTime();
            if (expireTime < authConfig.renewalTokenExpireTime) {
                AuthEntity auth = new AuthEntity();
                auth.safeToken = jwt.getClaim("safeToken").asString();
                auth.timestamp = System.currentTimeMillis();
                auth.userInfo = jwt.getClaim("userInfo").asMap();
                token = sign(auth);
            }

        }catch (Exception e) {
            throw new RuntimeException(e);
        }
        return token;

    }

    /**
     * 生成签名
     *
     * @return java.lang.String 返回加密的Token
     */
    public  String sign(AuthEntity authEntity) throws Exception {
        try {
            // 帐号加JWT私钥加密
            String secret = authEntity.safeToken + Base64Utils.decodeThrowsException(AuthManager.authConfig.perturbation);
            // 此处过期时间是以毫秒为单位，所以乘以1000
            Date date = new Date(System.currentTimeMillis() + AuthManager.authConfig.accessTokenExpireTime);
            Algorithm algorithm = Algorithm.HMAC256(secret);
            // 附带account帐号信息
            return JWT.create()
                    .withClaim("safeToken", authEntity.safeToken)
                    .withClaim("timestamp", authEntity.timestamp)
                    .withClaim("userInfo", authEntity.userInfo)
                    .withExpiresAt(date)
                    .sign(algorithm);
        } catch (UnsupportedEncodingException e) {
            throw new Exception("JWTToken加密出现UnsupportedEncodingException异常:" + e.getMessage());
        }
    }

}
