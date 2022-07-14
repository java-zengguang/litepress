package com.zg.mvc.servlet;

import com.zg.common.util.CommonUtil;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.eclipse.jetty.util.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JettyBoot {
    private static final Logger logger = LoggerFactory.getLogger(JettyBoot.class);
    private int port=8080;

    public JettyBoot() {
    }

    public JettyBoot(int port) {
        this.port = port;
    }

    public void doMain() {
        //创建服务器
        Server server = new Server(port);
        try {

            //默认servlet
/*
            if (true) {
                ServletContextHandler contextHandler = new ServletContextHandler();
                contextHandler.setContextPath("/static");
                contextHandler.setBaseResource(Resource.newResource(FileUtils.PATH+"static"));
                DefaultServlet defaultServlet = new DefaultServlet();
                ServletHolder defaultServletHolder = new ServletHolder("default", defaultServlet);
                defaultServletHolder.setInitParameter("dirAllowed", "true");
                // Use request pathInfo, don't calculate from contextPath
                defaultServletHolder.setInitParameter("pathInfoOnly", "true");
                contextHandler.addServlet(defaultServletHolder, "/static/*");
                server.setHandler(contextHandler);

            }
*/

            if (true) {
                ServletHandler servletHandler = new ServletHandler();
                servletHandler.addServletWithMapping(AdapterServlet.class, "/");
                server.insertHandler(servletHandler);
            }

            if(true){
                ResourceHandler resourceHandler=new ResourceHandler();
                resourceHandler.setBaseResource( Resource.newResource(CommonUtil.PATH+"static"));
                resourceHandler.setPathInfoOnly(true);
                resourceHandler.setDirAllowed(true);
                server.insertHandler(resourceHandler);
            }


            ServerConnector connector=server.getBean(ServerConnector.class);
            connector.setIdleTimeout(24*60*60*1000);
            //启动服务器
            server.start();
            //阻塞Jetty server的线程池，直到线程池停止
            server.join();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }


    public static void main(String[] args) {

        JettyBoot jettyBoot = new JettyBoot();
        jettyBoot.doMain();
    }
}