package com.zg.mvc.util.io;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by Administrator on 2019/3/12 0012.
 */
public class NIOUtils {

    private static int size = 1024;

    //使用nio读取raf
    public static int newReadFile(File file) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        FileChannel channel = raf.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(10);
        int readByte = 0;
        while (readByte != -1) {
            readByte = channel.read(buffer);
            System.out.println("readByte==" + readByte);
            buffer.flip();   //buffer模式反转，由写模式变为读模式

            //显示buffer的数据
            while (buffer.hasRemaining()) {  //判断是否到头
                System.out.print((char) buffer.get());
            }
            buffer.clear();  //清空整个缓存
            //buffer.compact(); //只清除已读取的数据
            System.out.println();
        }
        raf.close();
        return 0;
    }


    public static int newWriterFile(File file) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        FileChannel channel = raf.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(100);
        int writerByte = 0;
        buffer.put("1232300000000000".getBytes());
        buffer.flip();
        writerByte = channel.write(buffer);
        System.out.print(writerByte);

        channel.close();
        raf.close();
        return 0;
    }


    //用于处理存储临时上传文件
    public static int createTemporaryFile(InputStream inputStream, File file) {

        byte[] bytes = new byte[size];
        FileOutputStream bos = null;
        try {
            IOUtils.createFile(file);
            bos = new FileOutputStream(file);
            FileChannel channel = bos.getChannel();
            int current = 0;
            ByteBuffer buffer = ByteBuffer.allocate(size);
            while ((current = inputStream.read(bytes, 0, size)) != -1) {
                buffer.put(bytes, 0, current);
                buffer.flip();
                channel.write(buffer);
                buffer.clear();
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


    public static void main(String args[]) throws IOException {
        File file = new File("F:\\1.txt");
        //newReadFile(file);
        newWriterFile(file);
    }
}
