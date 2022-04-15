package com.zg.mvc.servlet;

import org.apache.catalina.filters.CorsFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import java.util.EnumSet;
import java.util.Set;

public class AServletContainerInitializer implements ServletContainerInitializer {
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Override
    public void onStartup(Set<Class<?>> set, ServletContext servletContext) throws ServletException {

        if (true) {
            logger.info("加载servlet");
            ServletRegistration.Dynamic servletDynamic = servletContext.addServlet("AdapterServlet", new AdapterServlet());
            servletDynamic.addMapping("/");
        }
        if (true) {
            logger.info("加载Filter");
            FilterRegistration.Dynamic corsFilter = servletContext.addFilter("CorsFilter", new CorsFilter());
            corsFilter.setInitParameter("cors.allowed.origins", "*");
            corsFilter.addMappingForServletNames(EnumSet.of(DispatcherType.REQUEST, DispatcherType.ASYNC), false, "AdapterServlet");
            corsFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.ASYNC, DispatcherType.INCLUDE, DispatcherType.FORWARD, DispatcherType.ERROR), false, "/*");
        }
    }
}
