package com.zg.network.common.fileservcie;

import java.io.File;

public class ServiceTest {
    public static void main(String args[]){
        File file=new File("D:\\test\\out1.txt");
        ReceiveFile baseFileService=new ReceiveFile(10000,file);
        Thread thread=new Thread(baseFileService);
        thread.start();
    }
}
