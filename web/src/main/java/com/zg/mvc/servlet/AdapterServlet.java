package com.zg.mvc.servlet;

import com.zg.common.init.Config;
import com.zg.mvc.adapter.ControllerAdapter;
import com.zg.mvc.auth.AuthManager;
import com.zg.mvc.entity.MVCOption;
import com.zg.mvc.util.ThreadLocalCache;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.tinylog.Logger;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Administrator on 2018/11/30 0030.
 */
@WebServlet(name = "AdapterServlet", urlPatterns = "/")
public class AdapterServlet extends HttpServlet {

    private static MVCOption mvcOption = (MVCOption) Config.getConfig("MVCOption");

    private static AuthManager authManager = new AuthManager();



    //权限校验
    private boolean isPower(HttpServletRequest request) {
        try {
            if (authManager.isWhite(request)) {
                return true;
            }
            String token = request.getHeader("token");
            if (token == null || "".equals(token)) {
                Cookie[] cookies = request.getCookies();
                for (Cookie cookie : cookies) {
                    if ("token".equals(cookie.getName())) {
                        token = cookie.getValue();
                    }
                }
            }
            if (token != null) {
                Map userInfo = authManager.verify(token);
                if (userInfo != null) {
                    Logger.info("验签通过" + userInfo);
                    ThreadLocalCache.setCache("currentUserInfo", userInfo);
                    return true;
                }
            }

        } catch (Exception e) {
            Logger.info(e.getMessage());
            return false;
        }
        return false;
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        if ("1".equals(mvcOption.powerLevel) && !isPower(request)) {
            response.setStatus(401);
            Cookie newCookie = new Cookie("token", ""); //假如要删除名称为username的Cookie JSESSIONID是cookie名 记得换成要删除的
            newCookie.setMaxAge(0); //立即删除型
            newCookie.setPath("/");
            response.addCookie(newCookie); //重新写入，将覆盖之前的

        } else {
            request.setCharacterEncoding("UTF-8");//传值编码
            response.setContentType("text/html;charset=UTF-8");//设置传输编码
            ControllerAdapter.resovleRequest(request, response);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
