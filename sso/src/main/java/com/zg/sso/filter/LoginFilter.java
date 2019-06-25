package com.zg.sso.filter;


import com.zg.init.Config;
import com.zg.sso.common.BaseLogin;
import com.zg.sso.common.SSOAdapter;
import com.zg.sso.entity.SSOOpthion;
import com.zg.sso.service.LoginService;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2019/2/13 0013.
 */
public  class LoginFilter implements Filter {

    BaseLogin baseLogin=new BaseLogin();

    private SSOOpthion ssoOpthion = (SSOOpthion) Config.getConfig("SSOOpthion");

    private SSOAdapter ssoAdapter=SSOAdapter.getInstanse(ssoOpthion,baseLogin);


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        if(ssoAdapter.doFilter(servletRequest,servletResponse)){
            filterChain.doFilter(servletRequest,servletResponse);
        }

    }

    @Override
    public void destroy() {

    }
}
