package com.zg.sso.controller;

import com.zg.common.util.reflect.JsonUtils;
import com.zg.incache.util.RomCacheInte;
import com.zg.incache.util.RomCacheUtil;
import com.zg.mvc.annotation.controller.Controller;
import com.zg.mvc.annotation.controller.ResultMapping;
import com.zg.mvc.controller.BaseController;
import com.zg.mvc.entity.MessageBean;
import com.zg.sso.common.WebCacheLogin;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by Administrator on 2019/2/12 0012.
 */
@Controller("/sso")
public class CacheLoginController extends BaseController {

    public WebCacheLogin loginService = WebCacheLogin.getInstance();
    private final RomCacheInte romCache = RomCacheUtil.getRomCache("LoginCache");

    @ResultMapping("/toLogin.do")
    public String toLogin() {

        return "privateURL::/html/Wopop.html";
    }

    @ResultMapping("/login.do")
    public String login(String username, String password, HttpServletRequest request, HttpServletResponse response) throws IllegalAccessException, NoSuchFieldException, SQLException, IOException {

        //登陆逻辑获取id
        String uuid = "1";
        //登陆逻辑获取id
        MessageBean messageBean = (MessageBean) loginService.doLogin(request, uuid, response);
        return "json::" + JsonUtils.objectToJson(messageBean);
    }


}
