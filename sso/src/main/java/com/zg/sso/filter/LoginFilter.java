package com.zg.sso.filter;


import com.zg.common.init.Config;
import com.zg.sso.common.LoginInte;
import com.zg.sso.common.SSOAdapter;
import com.zg.sso.common.SimpleLogin;
import com.zg.sso.entity.SSOOpthion;

import javax.servlet.*;
import java.io.IOException;

/**
 * Created by Administrator on 2019/2/13 0013.
 */
public class LoginFilter implements Filter {

    LoginInte simpleLogin = new SimpleLogin();

    private SSOOpthion ssoOpthion = (SSOOpthion) Config.getConfig("SSOOpthion");

    private SSOAdapter ssoAdapter = SSOAdapter.getInstanse(ssoOpthion, simpleLogin);


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        if (ssoAdapter.doFilter(servletRequest, servletResponse)) {
            filterChain.doFilter(servletRequest, servletResponse);
        }

    }

    @Override
    public void destroy() {

    }
}
