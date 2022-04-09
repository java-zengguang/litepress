package com.zg.admin.controller;

import com.zg.admin.entity.User;
import com.zg.admin.service.UserService;
import com.zg.admin.service.UserServiceInte;
import com.zg.handler.CommitInterfaceHandler;
import com.zg.mvc.annotation.controller.Controller;
import com.zg.mvc.annotation.controller.ResultMapping;
import com.zg.mvc.controller.BaseController;
import com.zg.mvc.entity.MessageBean;
import com.zg.util.reflect.JsonUtils;

import java.lang.reflect.Proxy;
import java.util.List;

/**
 * Created by Administrator on 2019/3/13 0013.
 */
@Controller("/user")
public class UserController extends BaseController {

    private UserServiceInte userService = (UserServiceInte) Proxy.newProxyInstance(UserService.class.getClassLoader(), UserService.class.getInterfaces(), new CommitInterfaceHandler(new UserService(), "insertUser,deleteUsers,editUser"));

    @ResultMapping("/initUserManager.do")
    public String initUserManager() throws Exception {
        List list = userService.getUserList(new User());
        json = new MessageBean("成功", true, list);
        String jsonS = JsonUtils.objectToJson(json).toString();
        return "json::" + jsonS;
    }

    @ResultMapping("/getUserList.do")
    public String getUserList(User user) throws Exception {
        List list = userService.getUserList(user);
        json = new MessageBean("成功", true, list);
        String jsonS = JsonUtils.objectToJson(json).toString();
        return "json::" + jsonS;
    }


    @ResultMapping("/insertUser.do")
    public String insertUser(User user) throws Exception {
        userService.insertUser(user);
        json = new MessageBean("成功", true, null);
        String jsonS = JsonUtils.objectToJson(json).toString();
        return "json::" + jsonS;
    }

    @ResultMapping("/deleteUsers.do")
    public String deleteUsers(String ids) throws Exception {
        userService.deleteUsers(ids);
        json = new MessageBean("成功", true, null);
        String jsonS = JsonUtils.objectToJson(json).toString();
        return "json::" + jsonS;
    }


    @ResultMapping("/editUser.do")
    public String editUser(User user) throws Exception {
        userService.editUser(user);
        json = new MessageBean("成功", true, null);
        String jsonS = JsonUtils.objectToJson(json).toString();
        return "json::" + jsonS;
    }


}
