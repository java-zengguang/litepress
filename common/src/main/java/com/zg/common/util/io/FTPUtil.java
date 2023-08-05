package com.zg.common.util.io;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.tinylog.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;

/**
 * Created by Administrator on 2019/1/7 0007.
 */
public class FTPUtil {


    public static FTPClient getFTPClient(String ftpHost, String ftpUserName,
                                         String ftpPassword, int ftpPort) {
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient = new FTPClient();
            ftpClient.connect(ftpHost, ftpPort);// 连接FTP服务器
            ftpClient.login(ftpUserName, ftpPassword);// 登陆FTP服务器
            if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                Logger.info("未连接到FTP，用户名或密码错误。");
                ftpClient.disconnect();
            } else {
                Logger.info("FTP连接成功----" + ftpHost + ":" + ftpPort);
            }
        } catch (SocketException e) {
            Logger.error(e);
        } catch (IOException e) {
            Logger.error(e);
        }
        return ftpClient;
    }


    public static void downloadFtpFile(String ftpHost, String ftpUserName,
                                       String ftpPassword, int ftpPort, String ftpPath, OutputStream outputStream,
                                       String fileName) {

        FTPClient ftpClient = null;

        try {
            ftpClient = getFTPClient(ftpHost, ftpUserName, ftpPassword, ftpPort);
            ftpClient.setControlEncoding("UTF-8"); // 中文支持
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.enterLocalPassiveMode();
            ftpClient.changeWorkingDirectory(ftpPath);
            ftpClient.retrieveFile(fileName, outputStream);
            outputStream.close();
            ftpClient.logout();

        } catch (FileNotFoundException e) {
            Logger.error(e);
        } catch (SocketException e) {
            Logger.error(e);
        } catch (IOException e) {
            Logger.error(e);
            Logger.error(e);
        }

    }


    public static boolean uploadFile(String ftpHost, String ftpUserName,
                                     String ftpPassword, int ftpPort, String ftpPath,
                                     String fileName, InputStream input) {
        boolean success = false;
        FTPClient ftpClient = null;
        try {
            int reply;
            ftpClient = getFTPClient(ftpHost, ftpUserName, ftpPassword, ftpPort);
            reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                return success;
            }
            ftpClient.setControlEncoding("UTF-8"); // 中文支持
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.enterLocalPassiveMode();
            ftpClient.changeWorkingDirectory(ftpPath);

            ftpClient.storeFile(fileName, input);

            input.close();
            ftpClient.logout();
            success = true;
        } catch (IOException e) {
            Logger.error(e);
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (IOException ioe) {
                }
            }
        }
        return success;
    }


}
