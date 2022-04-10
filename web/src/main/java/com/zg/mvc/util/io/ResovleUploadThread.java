package com.zg.mvc.util.io;

import com.zg.mvc.entity.SimpleFileEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 * Created by Administrator on 2019/1/9 0009.
 */
public class ResovleUploadThread {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResovleUploadThread.class);
    public RandomAccessFile raf;   //文件指针

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


    private SimpleFileEntity analysisFormHead(String formHead) {
        SimpleFileEntity simpleFileEntity = new SimpleFileEntity();
        String[] params = formHead.split("; ");
        simpleFileEntity.contentDisposition = (params[0].split(":"))[1].trim().replace("\"", "");
        simpleFileEntity.paramName = (params[1].split("="))[1].trim();
        simpleFileEntity.fileName = (params[2].split("="))[1].trim().replace("\"", "");
        return simpleFileEntity;
    }


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
                    return startPoint - 3;
                }
                endPoint = startPoint;
            }
        }
        return 0;
    }


    public SimpleFileEntity execate(HttpServletRequest request, String inputFilePath) throws IOException, InterruptedException {
        InputStream inputStream = request.getInputStream();
        String contentType = request.getContentType();
        String targetS = contentType.substring(contentType.lastIndexOf("boundary=") + 9, contentType.length());
        String inputFileName = targetS;
        //生成临时文件
        if (IOUtils.createTemporaryFile(inputStream, inputFilePath, inputFileName) == -1) {
            LOGGER.info("文件上传失败");
        }
        File inputFile = new File(inputFilePath, inputFileName);
        raf = new RandomAccessFile(inputFile, "r");
        long startPoint = getStartPointFile(0, raf.length(), targetS);
        long writerEnd = getEndPointFile(targetS);
        raf.seek(startPoint);
        String formHead = raf.readLine();   //获取表单头信息
        SimpleFileEntity fileEntity = analysisFormHead(formHead);//分析表头信息
        fileEntity.fileParentPath = inputFilePath;
        raf.readLine();
        raf.readLine();
        long writerStart = raf.getFilePointer();   //得到需要写入的信息


        File targetFile = new File(fileEntity.fileParentPath, fileEntity.fileName);
        IOUtils.createFile(targetFile);
        LOGGER.info("OUTPUT线程开始");
        outputThread(writerStart, writerEnd, inputFile, targetFile);
        LOGGER.info("OUTPUT线程结束");
        raf.close();
        inputFile.delete();
        fileEntity.filePath = targetFile.getAbsolutePath();
        return fileEntity;
    }
}



