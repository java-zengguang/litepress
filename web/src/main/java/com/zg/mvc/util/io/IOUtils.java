package com.zg.mvc.util.io;

import java.io.*;

/**
 * Created by Administrator on 2019/1/2 0002.
 */
public class IOUtils {


    private static int size = 64 * 1024;


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






    /*public static void extractFile(File temporaryFile,File targetFile) throws IOException {

        createFile(targetFile);
        RandomAccessFile raf=new RandomAccessFile(temporaryFile,"r");
        //InputStreamReader isr=new InputStreamReader(new FileInputStream(temporaryFile));
        //BufferedReader br=new BufferedReader(isr);
        BufferedOutputStream bos=new BufferedOutputStream(new FileOutputStream(targetFile));
        int count=0;
        int startOrEnd=0;  //1  start   0 end
        int writerFlag=0;  //1 准备写  0 不写  2准备 写
        int oneLine=0;    //1 online  0 no
        int typeFlag=0;   //0 不写  ，1  写文件 ，2 写表单
        String line="";
        int startPoint=0;
        int endPoint=0;
        byte[] b=null;
        while (raf.getFilePointer()<raf.length()) {

            if((raf.readByte())=='\r' && (raf.readByte())=='\n' ){

                endPoint=(int)raf.getFilePointer();
                if(writerFlag>1){
                    writerFlag--;
                }else{
                    raf.seek(startPoint);   //将指针放回去
                    b=new byte[endPoint-startPoint];
                    raf.read(b,0,endPoint-startPoint);   //读取
                    line=new String(b);
                    oneLine=1;  //获得完整一行
                }

                startPoint=endPoint;
            }else{
                oneLine=0;  //不是完整一行
                //writerFlag=0;  //重置可写标志
                //typeFlag=0;   //重置类型标志
                line="";
            }
            if (oneLine==1 && line.contains("-----------------------------")) {   //分隔标记
                count++;
                if(count%2==1){
                   startOrEnd=1;   //表单开始
                }else{
                    startOrEnd=0;   //表单结束
                    writerFlag=0;   //关闭可写
                }
            }

            if(oneLine==1 &&  line.contains("Content-Disposition: form-data") && line.contains("filename=")){
                typeFlag=1;
                writerFlag=3;    //到达读写位置，准备写
                continue;
            }


            if (oneLine==1 &&  startOrEnd==1 && writerFlag==1) {
                switch (typeFlag) {
                    case 0: {
                        break;
                    }
                    case 1: {
                        logger.info(line);
                        bos.write(b);
                        break;
                    }
                    case 2: {
                        break;
                    }
                }
            }

            if(  startOrEnd==1 && writerFlag==0){
                switch (typeFlag) {
                    case 0: {
                        break;
                    }
                    case 1: {
                        bos.flush();
                        bos.close();
                        logger.info("关闭流");
                        break;
                    }
                    case 2: {
                       // typeFlag=0;
                        break;
                    }
                }
            }
        }

        raf.close();
    }
*/


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
                    logger.info(name + "=" + value);
                }else{
                    String filename = item.getName();
                    logger.info(filename);
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
