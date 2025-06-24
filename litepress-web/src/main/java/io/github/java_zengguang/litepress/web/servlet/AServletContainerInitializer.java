package io.github.java_zengguang.litepress.web.servlet;


import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;
import jakarta.servlet.annotation.HandlesTypes;
import org.tinylog.Logger;

import java.util.Set;

@HandlesTypes(AServletContainerInitializer.class)
public class AServletContainerInitializer implements ServletContainerInitializer {

    @Override
    public void onStartup(Set<Class<?>> set, ServletContext servletContext) throws ServletException {


        Logger.info("加载servlet");

        ServletRegistration.Dynamic servletDynamic = servletContext.addServlet("AdapterServlet", new AdapterServlet());
        servletDynamic.addMapping("/");


        ServletRegistration.Dynamic sourceServlet = servletContext.addServlet("AEventSourceServlet", new AdapterServlet());
        servletDynamic.addMapping("/sse");

    }
}
