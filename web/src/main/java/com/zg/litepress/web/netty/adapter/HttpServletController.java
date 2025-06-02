package com.zg.litepress.web.netty.adapter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public interface HttpServletController {

     void dealHttpRequest(HttpServletRequest request,HttpServletResponse response) throws Exception;

}
