package com.zg.sso.controller;

import com.zg.mvc.annotation.controller.Controller;
import com.zg.mvc.annotation.controller.ResultMapping;
import com.zg.mvc.controller.BaseController;
import com.zg.mvc.entity.MessageBean;
import com.zg.sso.utils.SSOUtils;
import com.zg.util.reflect.JsonUtils;
import com.zg.sso.service.LoginService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2019/2/12 0012.
 */
@Controller("/sso1")
public class LoginController extends BaseController {

    public LoginService loginService = new LoginService();

    @ResultMapping("/toLogin.do")
    public String toLogin() {

        return "privateURL::/html/Wopop.html";
    }

    @ResultMapping("/hello.do")
    public String hello() throws IllegalAccessException {

        return "json::" + JsonUtils.objectToJson(new MessageBean("hello",true,null));
    }

    @ResultMapping("/signOut.do")
    public String signOut(HttpServletRequest request,HttpServletResponse response) throws SQLException, NoSuchFieldException, IllegalAccessException {
        MessageBean messageBean=SSOUtils.signOut(request,response);
        return "redirect::"+messageBean.object;
    }

    @ResultMapping("/login.do")
    public String login(String username, String password, HttpServletRequest request, HttpServletResponse response) throws IllegalAccessException, NoSuchFieldException, SQLException, IOException {
        String uuid="1";
        MessageBean messageBean=SSOUtils.login(uuid,request,response);
        return "json::" + JsonUtils.objectToJson(messageBean);
    }


}
