package com.zg.litepress.web.util.io;

import org.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by Administrator on 2019/1/10 0010.
 * 多线程写入文件
 */
public class OutputFileThread implements Runnable {

    public File inputFile;
    public File targetFile;
    public long startRead;
    public long length;
    public int compareCount = 0;
    private int count = 0;

    public OutputFileThread(File inputFile, File targetFile, long startRead, long length) {
        this.inputFile = inputFile;
        this.targetFile = targetFile;
        this.startRead = startRead;
        this.length = length;
    }

    private byte[] inputFile(long start, long end) throws IOException {
        RandomAccessFile iraf = new RandomAccessFile(inputFile, "r");
        int length = (int) (end - start + 1);
        byte[] bytes = new byte[length];
        iraf.seek(startRead + start);
        iraf.read(bytes, 0, length);
        iraf.close();
        return bytes;
    }

    private void outputFile(long start, long end) throws IOException {
        //int length= (int) (end-start);
        byte[] bytes = inputFile(start, end);
        RandomAccessFile oraf = new RandomAccessFile(targetFile, "rw");
        oraf.seek(start);
        oraf.write(bytes, 0, bytes.length);
        oraf.close();
    }

    public synchronized int getCount() {
        return ++count;
    }

    public synchronized int getCompareCount() {
        return ++compareCount;
    }

    @Override
    public void run() {
        int count = getCount();
        long x = length / 4;
        long x1 = length % 4;
        long start = 0;
        long end = 0;
        if (count < 5) {
            start = (count - 1) * x;
            end = count * x - 1;
        } else if (count == 5) {
            start = (count - 1) * x;
            end = length;
        } else {
            return;
        }

        Logger.info("第" + count + "个子线程启动  start" + start + "   end" + end);
        try {
            outputFile(start, end);
        } catch (IOException e) {
            Logger.error(e);
        }

        getCompareCount();
        Logger.info("第" + count + "个子线程结束  start" + start + "   end" + end);
    }
}
