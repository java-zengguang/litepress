package com.zg.mvc.servlet;

import com.zg.mvc.adapter.ControllerAdapterInte;
import com.zg.mvc.adapter.SimpleControllerAdapter;
import jakarta.servlet.ServletException;
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

    ControllerAdapterInte simpleControllerAdapter = SimpleControllerAdapter.getInstance();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UUID uuid = UUID.randomUUID();
        ThreadContext.put("threadID",uuid);
        request.setCharacterEncoding("UTF-8");//传值编码
        response.setContentType("text/html;charset=UTF-8");//设置传输编码
        //ControllerAdapter.resovleRequest(request, response);

        simpleControllerAdapter.doMain(request, response);

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
