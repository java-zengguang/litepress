package com.zg.sso.controller;

import com.zg.mvc.annotation.controller.Controller;
import com.zg.mvc.annotation.controller.ResultMapping;
import com.zg.mvc.controller.BaseController;
import com.zg.mvc.entity.MessageBean;
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
@Controller("/sso")
public class LoginController extends BaseController {

    public LoginService loginService = new LoginService();

    @ResultMapping("/toLogin.do")
    public String toLogin() {

        return "privateURL::/html/Wopop.html";
    }

    @ResultMapping("/signOut.do")
    public String signOut(HttpServletRequest request,HttpServletResponse response) throws SQLException, NoSuchFieldException, IllegalAccessException {

        String token=getCookieValue("token",request);
        String uuid=getCookieValue("uuid",request);
        loginService.updateLoginInvalid(uuid,token);
        Map<String,String> map=loginService.getTokenValue(token,"1");
        clearCookie(response,"token",map.get("domain"),map.get("rootPath"));
        clearCookie(response,"uuid",map.get("domain"),map.get("rootPath"));
        String url=map.get("url");
        return "redirect::"+url;
    }

    @ResultMapping("/login.do")
    public String login(String username, String password, HttpServletRequest request, HttpServletResponse response) throws IllegalAccessException, NoSuchFieldException, SQLException, IOException {
        String url = null;
        String domain=null;
        String rootPath=null;
        List<Cookie> newCookieList=new ArrayList();
        String token=getCookieValue("token",request);

        if(token==null){
            System.out.println("token丢失");
        }else {
             Map<String,String> resultMap= loginService.login(password,username,token);
                url = resultMap.get("url");
                domain = resultMap.get("domain");
                rootPath = resultMap.get("rootPath");
                String  uuid=resultMap.get("uuid");
                if(uuid!=null){
                    Cookie userCookie = new Cookie("uuid", uuid);
                    newCookieList.add(userCookie);
                    Cookie tokenCookie = new Cookie("token", token);
                    newCookieList.add(tokenCookie);
                    json = new MessageBean(resultMap.get("message"), true, url);
                } else {
                    json = new MessageBean(resultMap.get("message"), false, url);
                }
                setCookies(response, newCookieList, domain, rootPath, url);

        }
        return "json::" + JsonUtils.objectToJson(json);
    }

    private String getCookieValue(String key,HttpServletRequest request){

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (key.equals(cookie.getName())) {
                     return cookie.getValue();
                }
            }
        }
        return null;
    }

    private Cookie getCookie(String key,HttpServletRequest request){

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (key.equals(cookie.getName())) {
                    return cookie;
                }
            }
        }
        return null;
    }

    private void setCookies(HttpServletResponse response, List<Cookie> cookieList, String domain, String rootPath, String url) throws IOException {
        for(Cookie cookie:cookieList) {
            cookie.setDomain(domain);
            cookie.setPath(rootPath);
            cookie.setMaxAge(30*60);
            response.addCookie(cookie);
        }
    }

    private void clearCookie(HttpServletResponse response,String key,String domain,String rootPath){
        Cookie cookie=new Cookie(key,"");
        cookie.setMaxAge(0);
        cookie.setPath(domain);
        cookie.setPath(rootPath);
        response.addCookie(cookie);
    }
}
