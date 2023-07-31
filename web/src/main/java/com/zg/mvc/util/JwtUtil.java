package com.zg.mvc.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.zg.common.init.Config;
import com.zg.mvc.entity.AuthConfig;
import com.zg.mvc.entity.UserInfo;

import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * @description 描述：JWT 工具类
 **/
public class JwtUtil {


    private static AuthConfig authEntity = (AuthConfig) Config.getConfig("AuthConfig");


    /**
     * 校验token是否正确
     *
     * @param token Token
     * @return boolean 是否正确
     */
    public static boolean verify(String token) throws Exception {
        boolean flag = false;
        try {
            // 帐号加JWT私钥解密
            String secret = getClaim(token, "safeToken") + Base64Utils.decode(authEntity.perturbation);
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT result = verifier.verify(token);
            flag = true;
            return true;
        } catch (IllegalArgumentException e) {
            throw new Exception("JWTToken认证解密IllegalArgumentException异常:" + e.getMessage());
        } finally {
            if (!flag) {
                return false;
            }
        }
    }

    /**
     * 获得Token中的信息无需secret解密也能获得
     *
     * @param token
     * @param claim
     * @return java.lang.String
     */
    public static String getClaim(String token, String claim) throws Exception {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim(claim).asString();
        } catch (JWTDecodeException e) {
            throw new Exception("解密Token中的公共信息出现JWTDecodeException异常:" + e.getMessage());
        }
    }

    /**
     * 获得Token中过期时间点
     *
     * @param token
     * @return java.lang.String
     */
    public static Date getExpireDate(String token) throws Exception {
        try {
            DecodedJWT jwt = JWT.decode(token);
            Date expiresAt = jwt.getExpiresAt();
            return expiresAt;
        } catch (JWTDecodeException e) {
            throw new Exception("获取Token过期时间异常:" + e.getMessage());
        }
    }

    /**
     * 获得Token中过期剩余时间
     *
     * @param token
     * @return java.lang.String
     */
    public static long getExpireTime(String token) throws Exception {
        try {
            DecodedJWT jwt = JWT.decode(token);
            long expireTime = jwt.getExpiresAt().getTime();
            return expireTime;
        } catch (JWTDecodeException e) {
            throw new Exception("获取Token过期时间异常:" + e.getMessage());
        }
    }


    /**
     * 生成签名
     *
     * @return java.lang.String 返回加密的Token
     */
    public static String sign(UserInfo userInfo) throws Exception {
        try {
            // 帐号加JWT私钥加密
            String secret = userInfo.safeToken + Base64Utils.decodeThrowsException(authEntity.perturbation);
            // 此处过期时间是以毫秒为单位，所以乘以1000
            Date date = new Date(System.currentTimeMillis() + authEntity.accessTokenExpireTime);
            Algorithm algorithm = Algorithm.HMAC256(secret);
            // 附带account帐号信息
            return JWT.create()
                    .withClaim("safeToken", userInfo.safeToken)
                    .withClaim("userCode", userInfo.userCode)
                    .withClaim("state", userInfo.state)
                    .withClaim("userName", userInfo.userName)
                    .withExpiresAt(date)
                    .sign(algorithm);
        } catch (UnsupportedEncodingException e) {
            throw new Exception("JWTToken加密出现UnsupportedEncodingException异常:" + e.getMessage());
        }
    }

    public static UserInfo getUserInfo(String token) throws Exception {
        try {
            DecodedJWT jwt = JWT.decode(token);
            UserInfo userInfo = new UserInfo();
            userInfo.safeToken = jwt.getClaim("safeToken").asString();
            userInfo.userCode = jwt.getClaim("userCode").asString();
            userInfo.state = jwt.getClaim("state").asString();
            userInfo.userName = jwt.getClaim("userName").asString();
            return userInfo;
        } catch (JWTDecodeException e) {
            throw new Exception("解密Token中的公共信息出现JWTDecodeException异常:" + e.getMessage());
        }
    }


}
