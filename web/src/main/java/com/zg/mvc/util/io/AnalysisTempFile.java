package com.zg.mvc.util.io;

import com.zg.mvc.entity.RequestParamEntity;
import com.zg.mvc.entity.SimpleFileEntity;

import java.io.*;
import java.util.*;

public class AnalysisTempFile {

    public static List<RequestParamEntity> analysis(File file, String str) throws IOException, InterruptedException {
        //1.分段  获取各段儿数据指针，分段解析
        RandomAccessFile raf = new RandomAccessFile(file, "r");
        List<Long> indexList = new ArrayList<>();
        Map<Long, Long> pointMap = new HashMap<>();//指针map   （start,end）
        String line = "";
        while ((line = raf.readLine()) != null) {
            if (line.equals(str)) {
                Long index = raf.getFilePointer();
                indexList.add(index - line.length());
                indexList.add(index);

            }
            if (line.equals(str+"--")) { //报文最后多个--\r\n 所以往前进3
                Long index = raf.getFilePointer()-3;
                indexList.add(index - line.length());
                indexList.add(index);

            }
        }
        raf.close();

        for (int i = 1; i < indexList.size() - 1; i = i + 2) {
            pointMap.put(indexList.get(i), indexList.get(i + 1));
        }

        //2.分段完成后分段处理逻辑
        List<RequestParamEntity> requestParamEntitieList = new ArrayList<>();
        Set<Map.Entry<Long, Long>> pointSet = pointMap.entrySet();
        for (Map.Entry<Long, Long> entry : pointSet) {
            requestParamEntitieList.add(analyParam(file, entry.getKey(), entry.getValue()));
        }

        return requestParamEntitieList;
    }

    private static Map<String, String> analysisFormHead(String formHead) {
        Map<String, String> formHeadMap = new HashMap<>();
        formHead = formHead.replaceAll(":", "=").replaceAll("\"","");
        String[] params = formHead.split("; ");
        for (String param : params) {
            String[] strs = param.split("=");
            formHeadMap.put(strs[0].trim(), strs[1].trim());
        }

        return formHeadMap;
    }


    public static void outputThread(long writerStart, long writerEnd, File inputFile, File targetFile) throws InterruptedException, IOException {
/*        OutputFileThread ot = new OutputFileThread(inputFile, targetFile, writerStart, writerEnd - writerStart);
        for (int i = 0; i < 5; i++) {
            new Thread(ot).start();
        }
        while (ot.compareCount < 5) {
            this.wait(10);
        }*/

        int length = (int) (writerEnd - writerStart);
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


    private static RequestParamEntity analyParam(File file, Long startPoint, Long endPoint) throws IOException, InterruptedException {

        RandomAccessFile raf = new RandomAccessFile(file, "r");
        raf.seek(startPoint);
        Map<String, String> formHeadMap = new HashMap<>();
        String line = "";
        while (!"".equals(line = raf.readLine())) {
            formHeadMap.putAll(analysisFormHead(line));//分析表头信息
        }
        startPoint = raf.getFilePointer();  //更新指针位置
        raf.close();

        String contentType = formHeadMap.get("Content-Type");
        RequestParamEntity requestParamEntity = new RequestParamEntity();
        requestParamEntity.formHeadMap=formHeadMap;
        requestParamEntity.name=formHeadMap.get("name");

        if (contentType != null) {
            SimpleFileEntity fileEntity = new SimpleFileEntity();
            fileEntity.fileName=formHeadMap.get("filename");
            fileEntity.fileParentPath = file.getParent();
            fileEntity.contentDisposition=formHeadMap.get("Content-Disposition");
            File targetFile = new File(fileEntity.fileParentPath, fileEntity.fileName);
            fileEntity.filePath= targetFile.getAbsolutePath();
            IOUtils.createFile(targetFile);
            outputThread(startPoint, endPoint, file, targetFile);
            // inputFile.delete();
            fileEntity.filePath = targetFile.getAbsolutePath();
            requestParamEntity.type = "file";
            requestParamEntity.value = fileEntity;
        } else {
            int length = (int) (endPoint - startPoint );
            byte[] bytes = new byte[length];
            RandomAccessFile rafw = new RandomAccessFile(file, "r");
            rafw.seek(startPoint);
            rafw.read(bytes);
            rafw.close();
            requestParamEntity.type = "param";
            requestParamEntity.value = new String(bytes);
        }



        return requestParamEntity;
    }
}
