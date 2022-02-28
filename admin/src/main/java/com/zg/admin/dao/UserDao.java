package com.zg.admin.dao;

import com.zg.admin.entity.User;
import com.zg.database.util.JDBCUtils;
import com.zg.database.util.ModelSQLUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/3/13 0013.
 */
public class UserDao {

    public List<User> getUserList(User user) throws Exception {
        String sql = "select *from user_user  ";
        if(user.id!=0) {
            sql = sql+ " where id=#{id}";
        }
        sql= ModelSQLUtils.dynamicSQL(sql,user);
        List list= JDBCUtils.select(sql,User.class);
        return list;
    }

    public void insertUser(User user) throws SQLException, IllegalAccessException {
        String sql="";
        List list=new ArrayList();
        list.add(user);
        JDBCUtils.insertTables(list,User.class,"user_user");
    }

    public void deleteUsers(String[] idArray) throws SQLException {
        List sqlList=new ArrayList();
        for(int i=0;i<idArray.length;i++){
            String id=idArray[i];
            String sql="delete from user_user where id="+id;
            sqlList.add(sql);
        }
        JDBCUtils.batchSql(sqlList);
    }

    public void editUser(User user) throws NoSuchFieldException, IllegalAccessException, SQLException {
        String sql="update user_user set email=#{email} , nickname=#{nickname} , phone=#{phone} ,role_id=#{role_id} where id=#{id} ";
        sql= ModelSQLUtils.dynamicSQL(sql,user);
        JDBCUtils.operation(sql);
    }

}
