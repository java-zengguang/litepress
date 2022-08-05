package com.zg.network.im.login;

import com.zg.login.inte.LoginServiceInte;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SimpleLoginService implements LoginServiceInte {
    @Override
    public String verification(String username, String passworld) {
        return null;
    }

    @Override
    public Map<String, String> login(String password, String username, String token) {
        Map map = new HashMap();
        map.put("token", token);
        map.put("uuid", username);
        return map;
    }

    @Override
    public String registToken(String url, String domain, String rootPath) {
        UUID random = UUID.randomUUID();
        return random.toString();
    }

    @Override
    public Integer isLogin(String token, String uuid) {
        return null;
    }

    @Override
    public Map<String, String> getTokenValue(String token) {
        return null;
    }

    @Override
    public Map<String, String> getTokenValue(String token, String del_flag) {
        return null;
    }

    @Override
    public void invalidToken(String token) {

    }

    @Override
    public Integer updateLoginValid(String uuid, String token) {
        return null;
    }

    @Override
    public Integer updateLoginInvalid(String uuid, String token) {
        return 1;
    }
}
