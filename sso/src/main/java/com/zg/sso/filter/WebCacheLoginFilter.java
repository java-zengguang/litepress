package com.zg.sso.filter;

import com.zg.common.init.Config;
import com.zg.sso.common.LoginInte;
import com.zg.sso.common.SSOAdapter;
import com.zg.sso.common.WebCacheLogin;
import com.zg.sso.entity.SSOOpthion;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

public class WebCacheLoginFilter implements Filter {
    LoginInte baseLogin = WebCacheLogin.getInstance();

    private final SSOOpthion ssoOpthion = (SSOOpthion) Config.getConfig("SSOOpthion");

    private final SSOAdapter ssoAdapter = SSOAdapter.getInstanse(ssoOpthion, baseLogin);


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        String uri = ((HttpServletRequest) servletRequest).getRequestURI();

        if (uri.contains("/sso/login.do") || uri.contains("/sso/toLogin.do") || ssoAdapter.doFilter(servletRequest, servletResponse)) {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }


    @Override
    public void destroy() {

    }
}
