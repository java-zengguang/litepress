package com.zg.network.common.fileservcie;

import java.io.File;

public class ClientTest {

    public static void main(String args[]){

        File file=new File("D:\\test\\lbprem.sql");
        SendFile sendFile =new SendFile(file,"10.1.82.63",10000);
        Thread thread=new Thread(sendFile);
        thread.start();
    }
}
