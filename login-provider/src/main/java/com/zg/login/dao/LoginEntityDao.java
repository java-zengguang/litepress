package com.zg.login.dao;

import com.zg.common.dao.database.BaseEntityDao;
import com.zg.common.util.reflect.ModelSQLUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2019/2/13 0013.
 */
public class LoginEntityDao extends BaseEntityDao {

    public List login(Map map) throws NoSuchFieldException, IllegalAccessException, SQLException, ClassNotFoundException {
        String sql = "select uuid from user_login where username='#{username}' and password='#{password}' ";
        sql = ModelSQLUtils.dynamicSQL(sql, map);
        List list = selectToMapList(sql);
        return list;
    }

    public int insertToken(Map map) throws SQLException, NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
        // MongoDBUtils.insertOneMap("loginToken",new Document(map));
        String sql = "insert  into user_token (token,url,domain,rootPath,del_flag) value ('#{token}','#{url}','#{domain}','#{rootPath}','0')   ";
        sql = ModelSQLUtils.dynamicSQL(sql, map);
        Integer x = operation(sql);
        return x;
    }


    public List<Map> selectToken(Map map) throws NoSuchFieldException, IllegalAccessException, SQLException, ClassNotFoundException {
        //List list= MongoDBUtils.getDocumentList("loginToken",map);
        String sql = "select * from user_token where  token='#{token}' and del_flag='#{del_flag}' ";
        sql = ModelSQLUtils.dynamicSQL(sql, map);
        List list = selectToMapList(sql);
        return list;
    }

    public int updateToken(Map map) {
        String sql = "update user_token  set uuid='#{uuid}',del_flag='0'  where  token='#{token}' and  url= '#{url}' and del_flag='1'   ";
        try {
            sql = ModelSQLUtils.dynamicSQL(sql, map);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        Integer x = null;
        try {
            x = operation(sql);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return x;
    }


    public void changeTokenStatus(String token, String s) {
        String sql = "";
        if (s != null && "0".equals(s)) {
            sql = "update user_token  set del_flag='0'  where  token='#{token}' del_flag='1'   ";
        }
        if (s != null && "1".equals(s)) {
            sql = "update user_token  set del_flag='1'  where  token='#{token}' and del_flag='0'   ";
        }
        Map map = new HashMap();
        map.put("token", token);
        try {
            sql = ModelSQLUtils.dynamicSQL(sql, map);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        try {
            operation(sql);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public List<Map> isLogin(String uuid, String token) throws SQLException, ClassNotFoundException {
        String sql = "select status from user_login where  uuid='" + uuid + "' and token_id='" + token + "' ";
        List list = selectToMapList(sql);
        return list;
    }

    public int updateLoginStatus(String uuid, String token, String key) throws SQLException, ClassNotFoundException {
        String sql = "";
        if (key != null && "invalid".equals(key)) {
            sql = "update user_login  set status='0',token_id='" + token + "'  where  uuid='" + uuid + "'   and status='1'   ";
        }
        if (key != null && "valid".equals(key)) {
            sql = "update user_login  set status='1' ,token_id='" + token + "' where  uuid='" + uuid + "'   and status='0'   ";
        }
        return operation(sql);
    }
}
