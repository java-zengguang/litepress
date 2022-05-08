package com.zg.mvc.controller;

import com.zg.common.bean.entity.Test;
import com.zg.mvc.annotation.controller.Controller;
import com.zg.mvc.annotation.controller.ResultMapping;
import com.zg.common.util.reflect.JsonUtils;
import org.apache.commons.collections.map.HashedMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/12/5 0005.
 */
@Controller("/firstController")
public class FirstController extends BaseController {


    @ResultMapping("/toHello.do")
    public String toHello(HttpServletRequest request, HttpServletResponse response) throws IllegalAccessException {

        List list = new ArrayList();
        list.add(new Test(1, "2", new Date()));
        list.add(new Test(2, "3", new Date()));
        Map map = new HashedMap();

        logger.info("开始转发");
        //return "forward::/firstController/hello.do";
        // return "staticURL::/WEB-INF/html/hello.html";
        return "json::" + JsonUtils.objectToJson(map);
    }


    @ResultMapping("/hello.do")
    public void hello(HttpServletRequest request, HttpServletResponse response) {
        PrintWriter out = null;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("hello");
        out.println("hello");
    }
}
