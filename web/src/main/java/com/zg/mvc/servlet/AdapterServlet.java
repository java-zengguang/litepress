package com.zg.mvc.servlet;


import com.zg.mvc.web.adapter.HttpServletControllerAdapter;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.tinylog.ThreadContext;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Administrator on 2018/11/30 0030.
 */
@WebServlet(name = "AdapterServlet", urlPatterns = "/")
public class AdapterServlet extends HttpServlet {

    HttpServletControllerAdapter simpleControllerAdapter = HttpServletControllerAdapter.getInstance();



    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws  IOException {
        UUID uuid = UUID.randomUUID();
        ThreadContext.put("threadID", uuid);
       // request.setCharacterEncoding("UTF-8");//传值编码
       // response.setContentType("text/html;charset=UTF-8");//设置传输编码
        //ControllerAdapter.resovleRequest(request, response);

        try {
            simpleControllerAdapter.dealHttpRequest(request, response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws  IOException {
        doPost(request, response);
    }


}
