package com.zg.sso.controller;

import com.zg.cache.util.RomCacheInte;
import com.zg.cache.util.RomCacheUtil;
import com.zg.mvc.annotation.controller.Controller;
import com.zg.mvc.annotation.controller.ResultMapping;
import com.zg.mvc.controller.BaseController;
import com.zg.mvc.entity.MessageBean;
import com.zg.sso.common.WebCacheLogin;
import com.zg.util.reflect.JsonUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by Administrator on 2019/2/12 0012.
 */
@Controller("/sso")
public class CacheLoginController extends BaseController {

    public WebCacheLogin loginService = WebCacheLogin.getInstance();
    private RomCacheInte romCache = RomCacheUtil.getRomCache("LoginCache");

    @ResultMapping("/toLogin.do")
    public String toLogin() {

        return "privateURL::/html/Wopop.html";
    }

    /*@ResultMapping("/signOut.do")
    public String signOut(HttpServletRequest request, HttpServletResponse response) throws SQLException, NoSuchFieldException, IllegalAccessException {

        String token = getCookieValue("token", request);
        String uuid = getCookieValue("uuid", request);
        UserLogin userLogin = loginService.logout(uuid, token);
        String url = "";
        if (userLogin != null) {
            clearCookie(response, "token", userLogin.domain, userLogin.rootPath);
            clearCookie(response, "uuid", userLogin.domain, userLogin.rootPath);
            url = userLogin.url;
        }
        return "redirect::" + url;
    }*/

    @ResultMapping("/login.do")
    public String login(String username, String password, HttpServletRequest request, HttpServletResponse response) throws IllegalAccessException, NoSuchFieldException, SQLException, IOException {

        //登陆逻辑获取id
        String uuid = "1";
        //登陆逻辑获取id
        MessageBean messageBean = (MessageBean) loginService.doLogin(request, uuid, response);
        System.out.println(1);
        return "json::" + JsonUtils.objectToJson(messageBean);
    }


}
