package com.zg.mvc.servlet;

import com.zg.common.init.Config;
import com.zg.mvc.adapter.ControllerAdapter;
import com.zg.mvc.entity.MVCOption;
import com.zg.mvc.entity.UserInfo;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.cacheonix.Cacheonix;
import org.cacheonix.cache.Cache;
import org.cacheonix.impl.DistributedCacheonix;
import org.tinylog.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2018/11/30 0030.
 */
@WebServlet(name = "AdapterServlet", urlPatterns = "/")
public class AdapterServlet extends HttpServlet {

    private static MVCOption mvcOption = (MVCOption) Config.getConfig("MVCOption");

    private static Cacheonix cacheonix = DistributedCacheonix.getInstance();  //分布式本地缓存，任意节点登录，所有节点授权
    private static Cache loginCache = cacheonix.getCache("loginCache");
    private static List<String> whiteList= Arrays.asList("/Login.do");

    //白名单校验
    private boolean isWhite(HttpServletRequest request){
        String pathInfo = request.getPathInfo();
        Logger.info("访问路径："+pathInfo);
        if(whiteList.contains(pathInfo)){
            return true;
        }
        return  false;
    }

    //权限校验
    private boolean isPower(HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if ("token".equals(cookie.getName())) {
                String token = cookie.getValue();
                if (token != null) {
                    UserInfo userInfo = (UserInfo) loginCache.get(token);
                    if (userInfo != null) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");//传值编码
        response.setContentType("text/html;charset=UTF-8");//设置传输编码
        if ("1".equals(mvcOption.powerLevel) && isPower(request)) {
            ControllerAdapter.resovleRequest(request, response);
        } else {
            response.setStatus(401);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
