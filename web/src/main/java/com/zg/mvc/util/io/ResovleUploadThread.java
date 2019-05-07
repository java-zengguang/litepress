package com.zg.mvc.util.io;

import java.io.*;
import java.util.Date;

/**
 * Created by Administrator on 2019/1/9 0009.
 */
public abstract class ResovleUploadThread implements Runnable {

    public Object object;     //主线程对象
    public String targetS;    //目标字符串
    public File inputFile;    //输入文件
    public String targetFilePath;   //目标文件路径
    public RandomAccessFile raf;   //文件指针
    public String targetFileName;  //目标文件名
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

    public String getFileName(String formHead) {
        int x = formHead.lastIndexOf(".");
        String suffix = formHead.substring(x, formHead.length() - 1);
        Date date = new Date();
        Long time = date.getTime();
        return time + suffix;
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


    public abstract  void execate() throws IOException, InterruptedException;

   /* private  synchronized void execate() throws IOException, InterruptedException {

        raf = new RandomAccessFile(inputFile, "r");
        long x = getStartPointFile(0, raf.length(), targetS);
        long writerEnd = getEndPointFile(targetS);
        raf.seek(x);
        String formHead = raf.readLine();
        raf.readLine();
        raf.readLine();
        long writerStart = raf.getFilePointer();
        targetFileName = getFileName(formHead);
        synchronized (object) {
            object.notify();
            System.out.println("唤醒主线程" + targetFileName);
        }
        notify();
        File targetFile = new File(targetFilePath, targetFileName);
        IOUtils.createFile(targetFile);
        System.out.println("OUTPUT线程开始");
        outputThread(writerStart, writerEnd, inputFile, targetFile);
        System.out.println("OUTPUT线程结束");
        raf.close();
        inputFile.delete();

    }*/


}


