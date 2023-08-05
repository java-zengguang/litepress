package com.zg.network.common.fileservcie;


import org.tinylog.Logger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.Socket;

public class SendFile implements Runnable {

    private File file;
    private String ip;
    private int port;

    public SendFile(File file, String ip, int port) {
        this.file = file;
        this.ip = ip;
        this.port = port;
    }

    private void execute() {
        try {
            byte[] b = new byte[10 * 1024];
            InetAddress inetAddress = InetAddress.getByName(ip);
            Socket socket = new Socket(inetAddress, port);
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
            BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());

            int bytesToRead;
            while ((bytesToRead = in.read(b)) != -1) {
                out.write(b, 0, bytesToRead);
            }
            in.close();
            out.flush();
            out.close();
            socket.close();
        } catch (Exception e) {
            Logger.error(e);
        }
    }


    @Override
    public void run() {
        execute();
    }

}
