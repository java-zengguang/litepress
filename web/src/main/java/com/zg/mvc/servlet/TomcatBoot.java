package com.zg.mvc.servlet;


import com.zg.util.io.FileUtils;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;

import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.servlets.DefaultServlet;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.apache.poi.hssf.record.formula.functions.T;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;


import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;

public class TomcatBoot {


    private static int port = 8080;
    private static String contextPath = "/";
    private String baseDir= FileUtils.PATH;

    public  void start() throws LifecycleException, ServletException {
        Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir(baseDir);
        tomcat.setPort(port);
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setPort(port);
        tomcat.setConnector(connector);
        tomcat.addWebapp(contextPath, baseDir);
        tomcat.enableNaming();


        if(true) {
            //创建上下文
            Context context = tomcat.addContext("/static", "../static");
            Wrapper servlet = Tomcat.addServlet(context, "default", new DefaultServlet());//注册Servlet
            servlet.setLoadOnStartup(1);//容器启动初始化Sevlet
            servlet.addMapping("/");
        }

        //手动创建
        //tomcat.getConnector();
        tomcat.start();
        tomcat.getServer().await();
    }



    public static void main(String args[]) throws ServletException, LifecycleException, IOException {

        TomcatBoot tomcatBoot=new TomcatBoot();
        tomcatBoot.start();
    }

}
