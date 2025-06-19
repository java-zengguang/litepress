package com.zg.mvc.util.io;

import com.zg.mvc.entity.RequestParamEntity;
import com.zg.mvc.entity.SimpleFileEntity;
import jakarta.servlet.http.HttpServletRequest;
import org.tinylog.Logger;

import java.io.*;
import java.util.List;

/**
 * Created by Administrator on 2019/1/9 0009.
 */
public class ResovleUploadThread {

    public RandomAccessFile raf;   //文件指针

    public long getStartPointFile(long start, long end, String targetS) throws IOException {
        raf.seek(start);
        String line = "";
        while (raf.getFilePointer() < end) {
            line = raf.readLine();
            if (line.contains(targetS)) {
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


    public synchronized void outputThread(long writerStart, long writerEnd, File inputFile, File targetFile) throws InterruptedException, IOException {

        int length = (int) (writerEnd - writerStart + 1);
        byte[] bytes = new byte[length];
        RandomAccessFile rf = new RandomAccessFile(inputFile, "r");
        rf.seek(writerStart);
        rf.read(bytes, 0, length);
        rf.close();
        if (!targetFile.exists()) {
            targetFile.createNewFile();
        }
        OutputStream outputStream = new FileOutputStream(targetFile);
        outputStream.write(bytes);
        outputStream.close();

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
                    Logger.info(line);
                    return startPoint - 3;
                }
                endPoint = startPoint;
            }
        }
        return 0;
    }


    public List<RequestParamEntity> execate(HttpServletRequest request, String inputFilePath) throws IOException, InterruptedException {
        InputStream inputStream = request.getInputStream();
        String contentType = request.getContentType();
        String targetS = "--" + contentType.substring(contentType.lastIndexOf("boundary=") + 9);
        //生成临时文件
        if (IOUtils.createTemporaryFile(inputStream, inputFilePath, targetS) == -1) {
            Logger.info("文件上传失败");
        }
        File inputFile = new File(inputFilePath, targetS);
        return AnalysisTempFile.analysis(inputFile, targetS);
    }
}



