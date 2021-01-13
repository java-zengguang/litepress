package com.zg.mvc.util;

import com.zg.mvc.util.io.IOUtils;
import com.zg.mvc.util.io.ResovleUploadThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

/**
 * Created by Administrator on 2019/1/11 0011.
 */
public class FileUpLoad {

    private static final Logger LOGGER= LoggerFactory.getLogger(FileUpLoad.class);
    private ResovleUploadThread th;
    public String fileAbsolutePath;
    public String fileLogitchPath;

    public FileUpLoad(String targetS,String inputFilePath,String inputFileName,String targetFilePath) {
        th = new ResovleUploadThread(this, targetS, inputFilePath, inputFileName, targetFilePath);
    }
    public synchronized void upload() throws InterruptedException {

        Thread t1=new Thread(th);
        t1.start();
        LOGGER.info("阻塞主线程");
        this.wait();
        LOGGER.info("主线程继续执行");
        fileAbsolutePath=th.targetFilePath+th.targetFileName;
        fileLogitchPath=th.scripName;
    }
}
