package com.zg.litepress.network.common.fileservcie;

import org.tinylog.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class ReceiveFile implements Runnable {

    private final int port;
    private final File file;

    public ReceiveFile(int port, File file) {
        this.port = port;
        this.file = file;
    }

    public void execute() throws IOException {

        if (file != null) {
            if (file.exists()) {

            } else {
                file.createNewFile();
            }
            byte[] b = new byte[10 * 1024];
            ServerSocket serverSocket = new ServerSocket(port);
            // 监听来自客户端的连接
            Socket socket = serverSocket.accept();

            BufferedInputStream in = new BufferedInputStream(socket.getInputStream());

            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));

            int bytesToRead;
            while ((bytesToRead = in.read(b)) != -1) {
                out.write(b, 0, bytesToRead);
            }
            in.close();
            out.flush();
            out.close();
            socket.close();
            serverSocket.close();

        } else {
            Logger.info("文件存储路径出错");
        }
    }

    @Override
    public void run() {
        try {
            execute();
        } catch (IOException e) {
            Logger.error(e);
        }
    }
}
