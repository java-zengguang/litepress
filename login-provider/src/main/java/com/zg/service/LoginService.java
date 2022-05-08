package com.zg.service;


import com.zg.common.dao.LoginEntityDao;
import com.zg.inte.LoginServiceInte;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.*;

/**
 * Created by Administrator on 2019/2/12 0012.
 */
public class LoginService implements LoginServiceInte {

    public final Logger logger = LoggerFactory.getLogger(this.getClass().getName());
    private LoginEntityDao loginDao = new LoginEntityDao();

    public String verification(String username, String passworld) {
        Map map = new HashedMap();
        map.put("username", username);
        map.put("password", passworld);
        List<Map> list = null;
        try {
            list = loginDao.login(map);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (list != null && list.size() > 0) {
            return (String) list.get(0).get("uuid");
        } else {
            return null;
        }
    }


    public Map<String, String> login(String password, String username, String token) {

        Map<String, String> map = getTokenValue(token);

        if (map != null) {
            String uuid = verification(username, password);


            if (uuid != null) {

                if (updateLoginValid(uuid, token) > 0) {
                    map.put("uuid", uuid);
                    map.put("message", "登陆成功");
                } else {
                    map.put("message", "用户已经登陆，锁定中");
                }
                invalidToken(token);
            } else {
                map.put("message", "用户名密码错误");
            }
        } else {
            map = new HashMap<>();
            map.put("message", "token失效");
        }
        return map;
    }

    public String registToken(String url, String domain, String rootPath) {
        UUID random = UUID.randomUUID();
        String token = random.toString();
        Map map = new HashMap<>();
        map.put("token", token);
        map.put("url", url);
        map.put("domain", domain);
        map.put("rootPath", rootPath);

        int x = 0;
        try {
            x = loginDao.insertToken(map);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (x > 0) {
            return token;
        } else {
            return null;
        }
    }


    public boolean updateToken(String uuid, String token, String url) {
        logger.info(uuid + token + url);
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
        if (x > 0) {
            return true;
        } else {
            return false;
        }
    }

    public Integer isLogin(String token, String uuid) {
        List<Map> list = new ArrayList<Map>();
        if (uuid == null || token == null) {
            return -1;
        }
        try {
            list = loginDao.isLogin(uuid, token);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
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
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }
        return 0;
    }

    public Integer updateLoginInvalid(String uuid, String token) {
        try {
            return loginDao.updateLoginStatus(uuid, token, "invalid");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return 0;
    }


}
