package com.zg.litepress.core.util;


import org.tinylog.Logger;

import java.io.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;


/**
 * Created by Administrator on 2018/11/27 0027.
 */
public class CommonUtil {



    public static String getRootPath() {
        //获取项目的相对路径
        String path = "";

        Logger.info("projectRootPath=" + System.getProperty("projectRootPath"));
        if (System.getProperty("projectRootPath") != null) {
            path = System.getProperty("projectRootPath");
        } else {
            path = System.getProperty("user.dir") + File.separator;
        }

        return path;
    }

    public static String getModulePath(Class classes) {
        String path = "";

        try {
            path = classes.getProtectionDomain().getCodeSource().getLocation().getPath();
            File file = new File(path);
            path = file.getPath();
            if (path.contains(".jar") || path.contains(".exe")) {
                path = file.getParent();
            }

            path = URLDecoder.decode(path, StandardCharsets.UTF_8);
            path = path + File.separator;
            Logger.info("---" + path);
        } catch (Exception var3) {

            var3.printStackTrace();
        }

        return path;
    }
    public static String getThisPath(Class classes) {
        String path = "";

        try {
            path = classes.getProtectionDomain().getCodeSource().getLocation().getPath();
            File file = new File(path);
            path = file.getPath();
            path = URLDecoder.decode(path, StandardCharsets.UTF_8);
            path = path + File.separator;
            Logger.info("---" + path);
        } catch (Exception var3) {
            Exception e = var3;
            Logger.error(e);
        }

        return path;
    }

    //读取文件,并把文件信息放入到Properties 容器中
    public static Properties read(String fileName) {
        Properties ps = new Properties();
        InputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(fileName));
            ps.load(in);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Logger.error(e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    Logger.error(e);
                }
            }
        }
        return ps;

    }



}
