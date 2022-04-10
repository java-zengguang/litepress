package com.zg.network.common.fileservcie;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class ReceiveFile implements Runnable {

    private static Logger logger = Logger.getLogger(ReceiveFile.class.getName());
    private int port;
    private File file;

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
            logger.info("文件存储路径出错");
        }
    }

    @Override
    public void run() {
        try {
            execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
