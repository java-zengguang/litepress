package com.zg.mvc.servlet;

import com.zg.mvc.servlet.AdapterServlet;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import java.util.Set;

public class AServletContainerInitializer implements ServletContainerInitializer {
    @Override
    public void onStartup(Set<Class<?>> set, ServletContext servletContext) throws ServletException {
        System.out.println("加载servlet");
        ServletRegistration.Dynamic dynamic = servletContext.addServlet("AdapterServlet", new AdapterServlet());
        dynamic.addMapping("/");
    }
}
