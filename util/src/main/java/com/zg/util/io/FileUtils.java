package com.zg.util.io;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Created by Administrator on 2018/11/27 0027.
 */
public class FileUtils {

    static {
        //获取项目的相对路径
        String path = "";

        try {

            if (FileUtils.class.getResource("/")!=null){
                path = FileUtils.class.getResource("/").toURI().getPath();
            }else{
               path= FileUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath();
               File file=new File(path);
               path=file.getParent();
               path = java.net.URLDecoder.decode(path, "UTF-8");
               path=path+"\\";

            }

        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println("----"+path);


        PATH = path;
    }

    public static final String PATH;

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
       // System.out.println("配置文件路径："+FileUtils.PATH + fileName);
        Document doc = reader.read(new File(FileUtils.PATH + fileName));
        Element root = doc.getRootElement();
        return root;
    }

    public static  Element getRootElement(String rootPath,String fileName) throws DocumentException {
        SAXReader reader = new SAXReader();
        // System.out.println("配置文件路径："+FileUtils.PATH + fileName);
        Document doc = reader.read(new File(rootPath+fileName));
        Element root = doc.getRootElement();
        return root;
    }

    public static List getClassFormPackage(String packageName, boolean isAnnotation, Class annotation) throws ClassNotFoundException {
        List classList = new ArrayList();
        String packagePath = PATH + packageName.replace(".", "/");
        System.out.println(FileUtils.class+"====packagePath"+packagePath);
        File packageFile = new File(packagePath);
        File files[] = packageFile.listFiles();
        for (File file : files) {
            if (!file.getName().endsWith(".class")) {
                continue;
            }

            //ClassLoader loader=Object.class.getClassLoader();
            String className = packageName + "." + file.getName().substring(0, file.getName().length() - 6);
            Class c = Class.forName(className);
            if (!isAnnotation || !c.isAnnotationPresent(annotation)) {
                continue;
            }
            classList.add(c);
        }
        return classList;
    }

    public static List getClassFormPackage(String packageName) throws ClassNotFoundException {
        List classList = new ArrayList();
        String packagePath = PATH + packageName.replace(".", "/");
        System.out.println(packagePath);
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
        }
        return classList;
    }



    public static void writeFileThread(OutputStream out,InputStream in){
        Thread  thread=new Thread(){
            @Override
            public void run() {
                super.run();

            }
        };
    }

}
