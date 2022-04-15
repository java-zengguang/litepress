package com.zg.util.io;

import com.zg.util.reflect.ClassUtil;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Created by Administrator on 2018/11/27 0027.
 */
public class FileUtils extends org.apache.commons.io.FileUtils {
    public static final String PATH;
    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class.getName());

    static {
        //获取项目的相对路径
        String path = "";

        try {

            if (FileUtils.class.getResource("/") != null) {
                path = FileUtils.class.getResource("/").toURI().getPath();
                logger.info("---" + path);
            } else {
                path = FileUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath();
                File file = new File(path);
                path = file.getParent();
                path = java.net.URLDecoder.decode(path, "UTF-8");
                path = path + "\\";

                logger.info("===" + path);
            }


        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        PATH = path;
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
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return ps;

    }


    public static Element getRootElement(String fileName) throws DocumentException {
        SAXReader reader = new SAXReader();
        // logger.info("配置文件路径："+FileUtils.PATH + fileName);
        Document doc = reader.read(new File(FileUtils.PATH + fileName));
        Element root = doc.getRootElement();
        return root;
    }

    public static Element getRootElement(String rootPath, String fileName) throws DocumentException {
        SAXReader reader = new SAXReader();
        // logger.info("配置文件路径："+FileUtils.PATH + fileName);
        Document doc = reader.read(new File(rootPath + fileName));
        Element root = doc.getRootElement();
        return root;
    }

    public static List getClassFormPackage(String packageName, boolean isAnnotation, Class annotation) throws ClassNotFoundException {

        List<Class> classList = getClassFormPackage(packageName);
        for (Class c : classList) {
            if (!isAnnotation || !c.isAnnotationPresent(annotation)) {
                continue;
            }
            classList.add(c);
        }

        return classList;
    }

    public static List getClassFormPackage(String packageName) throws ClassNotFoundException {
        List classList = new ArrayList();
/*        String packagePath = PATH + packageName.replace(".", File.separator);
        logger.info(packagePath);
        File packageFile = new File(packagePath);
        File files[] = packageFile.listFiles();
        for (File file : files) {
            if (!file.getName().endsWith(".class")) {
                continue;
            }

            //ClassLoader loader=Object.class.getClassLoader();
            String className = packageName + "." + file.getName().substring(0, file.getName().length() - 6);
            Class c = Class.forName(className);
            classList.add(c);
        }*/
        Set classSet = ClassUtil.getClasses(packageName);
        classList.addAll(classSet);
        return classList;
    }


    public static void writeFileThread(OutputStream out, InputStream in) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();

            }
        };
    }

}
