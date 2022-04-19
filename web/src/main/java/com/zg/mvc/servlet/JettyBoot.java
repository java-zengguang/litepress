package com.zg.mvc.servlet;

import com.zg.mvc.adapter.ControllerAdapter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class JettyBoot {
    private static final Logger logger = LoggerFactory.getLogger(ControllerAdapter.class);


    public void doMain() {
        //创建服务器
        Server server = new Server(8080);
        try {


            //默认servlet
            ServletContextHandler contextHandler = new ServletContextHandler();
            contextHandler.setContextPath("/");
            contextHandler.setBaseResource(Resource.newResource(Objects.requireNonNull(this.getClass().getClassLoader().getResource("static")).getPath()));
            DefaultServlet defaultServlet = new DefaultServlet();
            ServletHolder defaultServletHolder = new ServletHolder("default", defaultServlet);

            defaultServletHolder.setInitParameter("dirAllowed","true");
            // Use request pathInfo, don't calculate from contextPath
            defaultServletHolder.setInitParameter("pathInfoOnly","true");
            contextHandler.addServlet(defaultServletHolder,"/static/*");
            contextHandler.addServlet(AdapterServlet.class,"/");

            server.setHandler(contextHandler);

            //启动服务器
            server.start();
            //阻塞Jetty server的线程池，直到线程池停止
            server.join();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }


    public static void main(String[] args) {

        JettyBoot jettyBoot=new JettyBoot();
        jettyBoot.doMain();
    }
}