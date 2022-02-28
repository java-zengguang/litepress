package com.zg.admin.service;

import com.zg.admin.dao.UserDao;
import com.zg.admin.entity.User;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Administrator on 2019/3/13 0013.
 */
public class UserService implements UserServiceInte{

    private UserDao userDao= new UserDao();


    public List<User> getUserList(User user)  {
        List list=null;
        try {
          list= userDao.getUserList(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void insertUser(User user)  {
        try {
            userDao.insertUser(user);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void deleteUsers(String ids) throws SQLException {
        System.out.println("ids"+ids);
        String idArray[]=ids.split(",");
        System.out.println(idArray);
        userDao.deleteUsers(idArray);

    }

    @Override
    public void editUser(User user) {

        try {
            userDao.editUser(user);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
