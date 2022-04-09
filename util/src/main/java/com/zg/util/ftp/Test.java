package com.zg.util.ftp;


import com.zg.bean.entity.FTPConfig;
import com.zg.init.Config;

import java.io.*;

/**
 * Created by jyf on 2017/6/2.
 */
public class Test {
    public static void main(String[] args) throws FileNotFoundException {
        FTPConfig ftpConfig = (FTPConfig) Config.getConfig("FTPConfig");
        String localPath = "F:\\lis-web-grp-claim.war";
        String fileName = "lis-web-grp-claim.war";

        //上传一个文件
        try {
            FileInputStream in = new FileInputStream(new File(localPath));
            boolean test = FTPUtil.uploadFile(ftpConfig.ftpHost, ftpConfig.ftpUserName, ftpConfig.ftpPassword, ftpConfig.ftpPort, ftpConfig.ftpPath, fileName, in);
            System.out.println(test);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println(e);
        }

        //在FTP服务器上生成一个文件，并将一个字符串写入到该文件中
        try {
            fileName = "hello.txt";
            InputStream input = new ByteArrayInputStream("hello nihaoa ".getBytes("GBK"));
            boolean flag = FTPUtil.uploadFile(ftpConfig.ftpHost, ftpConfig.ftpUserName, ftpConfig.ftpPassword, ftpConfig.ftpPort, ftpConfig.ftpPath, fileName, input);
            System.out.println(flag);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //下载一个文件
        OutputStream out = new FileOutputStream(new File("F:\\hello.txt"));
        FTPUtil.downloadFtpFile(ftpConfig.ftpHost, ftpConfig.ftpUserName, ftpConfig.ftpPassword, ftpConfig.ftpPort, ftpConfig.ftpPath, out, fileName);
    }
}

