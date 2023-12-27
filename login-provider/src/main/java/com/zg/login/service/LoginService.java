package com.zg.login.service;


import com.zg.login.dao.LoginEntityDao;
import com.zg.login.inte.LoginServiceInte;
import org.apache.commons.collections.map.HashedMap;
import org.tinylog.Logger;

import java.sql.SQLException;
import java.util.*;

/**
 * Created by Administrator on 2019/2/12 0012.
 */
public class LoginService implements LoginServiceInte {

    private final LoginEntityDao loginDao = new LoginEntityDao();

    public String verification(String username, String passworld) {
        Map map = new HashedMap();
        map.put("username", username);
        map.put("password", passworld);
        List<Map> list = null;
        try {
            list = loginDao.login(map);
        } catch (NoSuchFieldException e) {
            Logger.error(e);
        } catch (IllegalAccessException e) {
            Logger.error(e);
        } catch (SQLException e) {
            Logger.error(e);
        } catch (ClassNotFoundException e) {
            Logger.error(e);
        }
        if (list != null && list.size() > 0) {
            return (String) list.get(0).get("uuid");
        } else {
            return null;
        }
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


    public boolean updateToken(String uuid, String token, String url) {
        Logger.info(uuid + token + url);
        if (uuid == null) {
            uuid = "";
        }
        if (token == null) {
            token = "";
        }
        if (url == null) {
            url = "";
        }
        Map map = new HashMap<>();
        map.put("token", token);
        map.put("url", url);
        map.put("uuid", uuid);
        int x = loginDao.updateToken(map);
        return x > 0;
    }

    public Integer isLogin(String token, String uuid) {
        List<Map> list = new ArrayList<Map>();
        if (uuid == null || token == null) {
            return -1;
        }
        try {
            list = loginDao.isLogin(uuid, token);
        } catch (SQLException | ClassNotFoundException e) {
            Logger.error(e);
        }

        if (list != null && list.size() > 0) {
            Map<String, String> map = list.get(0);
            return Integer.valueOf(map.get("status"));
        } else {
            return -1;
        }

    }

    public Map<String, String> getTokenValue(String token) {
        return getTokenValue(token, "0");
    }

    public Map<String, String> getTokenValue(String token, String del_flag) {
        Map map = new HashMap();
        map.put("token", token);
        map.put("del_flag", del_flag);
        Map<String, String> resultMap = null;
        List<Map> list = null;
        try {
            list = loginDao.selectToken(map);
        } catch (NoSuchFieldException e) {
            Logger.error(e);
        } catch (IllegalAccessException e) {
            Logger.error(e);
        } catch (SQLException e) {
            Logger.error(e);
        } catch (ClassNotFoundException e) {
            Logger.error(e);
        }
        if (list != null && list.size() > 0) {
            resultMap = list.get(0);
        }
        return resultMap;
    }

    public void invalidToken(String token) {
        loginDao.changeTokenStatus(token, "1");
    }

    public Integer updateLoginValid(String uuid, String token) {
        try {
            return loginDao.updateLoginStatus(uuid, token, "valid");
        } catch (SQLException | ClassNotFoundException e) {
            Logger.error(e);
        }
        return 0;
    }

    public Integer updateLoginInvalid(String uuid, String token) {
        try {
            return loginDao.updateLoginStatus(uuid, token, "invalid");
        } catch (SQLException | ClassNotFoundException e) {
            Logger.error(e);
        }

        return 0;
    }


}
