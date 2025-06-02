package com.zg.litepress.web.util.io;

import java.io.*;

/**
 * Created by Administrator on 2019/1/2 0002.
 */
public class IOUtils {


    private static final int size = 64 * 1024;


    public static int createFile(File file) throws IOException {
        if (file.exists()) {
            return 0;
        } else {
            if (file.createNewFile()) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    //用于文件下载
    public static int inputFile(OutputStream outputStream, File file) {
        byte[] bytes = new byte[size];
        BufferedInputStream inputStream = null;
        try {
            createFile(file);
            inputStream = new BufferedInputStream(new FileInputStream(file));
            int current = 0;
            while ((current = inputStream.read(bytes, 0, size)) != -1) {
                outputStream.write(bytes, 0, current);
            }
            inputStream.close();
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            return -1;
        }
        return 0;
    }


    //用于处理存储临时上传文件
    public static int createTemporaryFile(InputStream inputStream, File file) {
        byte[] bytes = new byte[size];
        BufferedOutputStream bos = null;
        try {
            createFile(file);
            bos = new BufferedOutputStream(new FileOutputStream(file));
            int current = 0;
            while ((current = inputStream.read(bytes, 0, size)) != -1) {
                bos.write(bytes, 0, current);
            }
            inputStream.close();
            bos.flush();
            bos.close();
        } catch (IOException e) {
            file.delete();
            return -1;
        }
        return 0;
    }

    //用于处理存储临时上传文件
    public static int createTemporaryFile(InputStream inputStream, String filePath, String fileName) {
        File file = new File(filePath, fileName);
        return createTemporaryFile(inputStream, file);
    }


}

/*

    private static void xx(RequestContext request, String filePath){
        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setHeaderEncoding("UTF-8");

        try{
            List<FileItem> list = upload.parseRequest(request);
            for(FileItem item : list){
                if(item.isFormField()){
                    String name = item.getFieldName();
                    String value = item.getString("UTF-8");
                    //value = new String(value.getBytes("iso8859-1"),"UTF-8");
                    Logger.info(name + "=" + value);
                }else{
                    String filename = item.getName();
                    Logger.info(filename);
                    if(filename==null || filename.trim().equals("")){
                        continue;
                    }
                    filename = filename.substring(filename.lastIndexOf("\\")+1);
                    InputStream in = item.getInputStream();
                    FileOutputStream out = new FileOutputStream(filePath + "\\" + filename);
                    byte buffer[] = new byte[1024];
                    int len = 0;
                    while((len=in.read(buffer))>0){
                        out.write(buffer, 0, len);
                    }
                    in.close();
                    out.close();
                    item.delete();
                }
            }
        }catch (Exception e) {

        }


    }*/
