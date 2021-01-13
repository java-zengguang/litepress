package com.zg.mvc.util.io;

import com.zg.mvc.util.FileUpLoad;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Date;

/**
 * Created by Administrator on 2019/1/9 0009.
 */
public class ResovleUploadThread implements Runnable {
    private static final Logger LOGGER= LoggerFactory.getLogger(ResovleUploadThread.class);
    public Object object;     //主线程对象
    public String targetS;    //目标字符串
    public File inputFile;    //输入文件
    public String targetFilePath;   //目标文件路径
    public RandomAccessFile raf;   //文件指针
    public String targetFileName;  //目标文件名
    public String scripName;  //逻辑名称
    public String suffix; //后缀名

    public String contentDisposition;
    public String name;
    public String fileName;

 //   Content-Disposition: form-data; name="fileName"; filename="2020年7、8月工作量统计-曾广 .xlsx"
    // private OutputStream outputStream;

    public ResovleUploadThread(Object object, String targetS, String inputFilePath, String inputFileName, String targetFilePath) {
        this.object = object;
        this.targetS = targetS;
        this.inputFile = new File(inputFilePath,inputFileName);
        this.targetFilePath = targetFilePath;
    }

    public long getStartPointFile(long start, long end, String targetS) throws IOException {
        raf.seek(start);
        String line = "";
        while (raf.getFilePointer() < end) {
            line = raf.readLine();
            if (line.contains(targetS)) {
                //resultEnd.add(raf.getFilePointer());
                //resultStart.add(raf.getFilePointer() - line.length() - 2);
                return raf.getFilePointer();
            }
        }
        return 0;

    }

/*    public String getSuffix(String formHead){
        int x = formHead.lastIndexOf(".");
        String suffix = formHead.substring(x, formHead.length() - 1);
        return suffix;
    }

    public String getFileName(String formHead) {
        int s=formHead.lastIndexOf("filename=\"");
        int x = formHead.lastIndexOf(".");
        String fileName= formHead.substring(s, x).replace("filename=\"","");
        return fileName ;
    }*/

    private void analysisFormHead(String formHead){
      String[] params= formHead.split("; ");
      contentDisposition=(params[0].split(":"))[1].trim().replace("\"","");
      name=(params[1].split("="))[1].trim();
      fileName=(params[2].split("="))[1].trim().replace("\"","");
      int x = fileName.lastIndexOf(".");
      suffix = fileName.substring(x);
      targetFileName = fileName.substring(0,x);

    }

/*
    public void output(long writerStart,long writerEnd,OutputStream outputStream) throws IOException {
        raf.seek(writerStart);
        int length = (int) (writerEnd - writerStart);
        byte[] b = new byte[length];
        raf.read(b, 0, length);
        System.out.println(new String(b));
        outputStream.write(b);
        outputStream.close();
    }*/


    public synchronized void outputThread(long writerStart, long writerEnd, File inputFile, File targetFile) throws InterruptedException {
        OutputFileThread ot = new OutputFileThread(inputFile, targetFile, writerStart, writerEnd - writerStart);
        for (int i = 0; i < 5; i++) {
            new Thread(ot).start();
        }
        while (ot.compareCount < 5) {
            System.out.println(ot.compareCount);
            this.wait(10);
        }

    }


    public long getEndPointFile(String targetS) throws IOException {
        int startPoint = (int) raf.length() - 1;
        int endPoint = (int) raf.length();
        raf.seek(raf.length());
        byte[] b = null;
        String line = "";
        while (raf.getFilePointer() > 0) {
            raf.seek(raf.getFilePointer() - 2);
            if ((raf.readByte()) == '\n') {
                startPoint = (int) raf.getFilePointer();
                raf.seek(startPoint);   //将指针放回去
                b = new byte[endPoint - startPoint];
                raf.read(b, 0, endPoint - startPoint);   //读取
                line = new String(b);
                if (line.contains(targetS)) {
                    System.out.println(line);
                    return startPoint-3;
                }
                endPoint = startPoint;
            }
        }
        return 0;
    }

    @Override
    public void run() {
        try {
            LOGGER.info("IO线程开启");
            execate();
            LOGGER.info("IO线程结束");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    public  synchronized void execate() throws IOException, InterruptedException {

        raf = new RandomAccessFile(inputFile, "r");
        long startPoint = getStartPointFile(0, raf.length(), targetS);
        long writerEnd = getEndPointFile(targetS);
        raf.seek(startPoint);
        String formHead = raf.readLine();   //获取表单头信息
        raf.readLine();
        raf.readLine();
        long writerStart = raf.getFilePointer();   //得到需要写入的信息
  /*      targetFileName = getFileName(formHead);
        suffix=getSuffix(formHead);*/
        analysisFormHead(formHead);
        Date date = new Date();
        Long time = date.getTime();
        scripName=targetFileName;
        synchronized (object) {
            object.notify();
            LOGGER.info("唤醒主线程" + targetFileName);
        }
        notify();
        File targetFile = new File(targetFilePath, targetFileName+suffix);
        IOUtils.createFile(targetFile);
        LOGGER.info("OUTPUT线程开始");
        outputThread(writerStart, writerEnd, inputFile, targetFile);
        LOGGER.info("OUTPUT线程结束");
        raf.close();
        //   inputFile.delete();

    }
}



