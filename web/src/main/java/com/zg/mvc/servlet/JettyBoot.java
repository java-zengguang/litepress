package com.zg.mvc.servlet;

import com.zg.common.annotation.ScanAnnotation;
import com.zg.common.util.CommonUtil;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.session.DefaultSessionIdManager;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.FilterMapping;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.eclipse.jetty.util.resource.Resource;
import org.tinylog.Logger;


public class JettyBoot {
    private int port = 8080;

    public JettyBoot() {
    }

    public JettyBoot(int port) {
        this.port = port;
    }

    public static void main(String[] args) {


        System.setProperty("projectRootPath", CommonUtil.getModulePath(JettyBoot.class));
        ScanAnnotation.scanModule(JettyBoot.class.getModule());
        JettyBoot jettyBoot = new JettyBoot();
        jettyBoot.doMain();
    }

    public void doMain() {
        //创建服务器
        Server server = new Server(port);
        try {

            //默认servlet
            SessionHandler sessionHandler = new SessionHandler();
            // 设置Session Manager的配置参数
            // 例如，设置Session超时时间为30分钟
            sessionHandler.setMaxInactiveInterval(1800);
            // 将Session Manager设置到Jetty服务器中
            server.setHandler(sessionHandler);

            if (true) {
                ServletHandler servletHandler = new ServletHandler();
                servletHandler.addServletWithMapping(AdapterServlet.class, "/");
                //过滤
                if (true) {
                    CrossOriginFilter crossOriginFilter = new CrossOriginFilter();
                    FilterHolder filterHolder = new FilterHolder();
                    filterHolder.setFilter(crossOriginFilter);
                    filterHolder.setClassName(CrossOriginFilter.class.getName());
                    filterHolder.setName("cross-origin");
                    filterHolder.setInitParameter("allowedOrigins", "*");
                    filterHolder.setInitParameter("allowedMethods", "GET,POST,OPTIONS,DELETE,PUT,HEAD");
                    // filterHolder.setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin");
                    filterHolder.setInitParameter("allowedHeaders", "*");

                    filterHolder.setInitParameter("allowCredentials", "true");
                    FilterMapping filterMapping = new FilterMapping();
                    filterMapping.setFilterName("cross-origin");
                    filterMapping.setPathSpec("/*");

                    servletHandler.addFilter(filterHolder, filterMapping);
                }
                server.insertHandler(servletHandler);
            }

            if (true) {
                ResourceHandler resourceHandler = new ResourceHandler();
                String path = CommonUtil.getRootPath();
                if (System.getProperty("projectRootPath") != null) {
                    path = System.getProperty("projectRootPath");
                }
                resourceHandler.setBaseResource(Resource.newResource(path + "static"));
                resourceHandler.setPathInfoOnly(true);
                resourceHandler.setDirAllowed(true);
                server.insertHandler(resourceHandler);
            }


            ServerConnector connector = server.getBean(ServerConnector.class);
            connector.setIdleTimeout(24 * 60 * 60 * 1000);
            //启动服务器
            server.start();
            //阻塞Jetty server的线程池，直到线程池停止
            server.join();
        } catch (Exception e) {
            Logger.error(e.getMessage(), e);
        }
    }
}