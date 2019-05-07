package com.zg.mvc.util;

import com.zg.mvc.util.io.IOUtils;
import com.zg.mvc.util.io.ResovleUploadThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by Administrator on 2019/1/11 0011.
 */
public class FileUpLoad {

    private static final Logger LOGGER= LoggerFactory.getLogger(FileUpLoad.class);

    public synchronized String upload(String targetS,String inputFilePath,String inputFileName,String targetFilePath) throws InterruptedException {
        ResovleUploadThread th= new ResovleUploadThread(this, targetS, inputFilePath, inputFileName, targetFilePath) {
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

            @Override
            public  synchronized void execate() throws IOException, InterruptedException {

                raf = new RandomAccessFile(inputFile, "r");
                long startPoint = getStartPointFile(0, raf.length(), targetS);
                long writerEnd = getEndPointFile(targetS);
                raf.seek(startPoint);
                String formHead = raf.readLine();   //获取表单头信息
                raf.readLine();
                raf.readLine();
                long writerStart = raf.getFilePointer();   //得到需要写入的信息
                targetFileName = getFileName(formHead);
                synchronized (object) {
                    object.notify();
                    LOGGER.info("唤醒主线程" + targetFileName);
                }
                notify();
                File targetFile = new File(targetFilePath, targetFileName);
                IOUtils.createFile(targetFile);
                LOGGER.info("OUTPUT线程开始");
                outputThread(writerStart, writerEnd, inputFile, targetFile);
                LOGGER.info("OUTPUT线程结束");
                raf.close();
                inputFile.delete();

            }
        };
        Thread t1=new Thread(th);
        t1.start();
        LOGGER.info("阻塞主线程");
        this.wait();
        LOGGER.info("主线程继续执行");
        String fileName=th.targetFileName;
        return targetFilePath+fileName;
    }
}
