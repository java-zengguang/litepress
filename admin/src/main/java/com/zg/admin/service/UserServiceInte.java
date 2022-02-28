package com.zg.admin.service;

import com.zg.admin.entity.User;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Administrator on 2019/3/14 0014.
 */
public interface UserServiceInte {
    List<User> getUserList(User user);
    void insertUser(User user);

    void deleteUsers(String ids) throws SQLException;

    void editUser(User user);
}
