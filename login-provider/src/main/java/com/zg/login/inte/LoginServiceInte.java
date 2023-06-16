package com.zg.login.inte;

import com.zg.common.annotation.Transaction;

import java.util.Map;

public interface LoginServiceInte {


    String verification(String username, String passworld);

    @Transaction
    Map<String, String> login(String password, String username, String token);

    @Transaction
    String registToken(String url, String domain, String rootPath);

    Integer isLogin(String token, String uuid);

    Map<String, String> getTokenValue(String token);

    Map<String, String> getTokenValue(String token, String del_flag);

    void invalidToken(String token);

    @Transaction
    Integer updateLoginValid(String uuid, String token);

    @Transaction
    Integer updateLoginInvalid(String uuid, String token);


}
