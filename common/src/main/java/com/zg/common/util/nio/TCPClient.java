package com.zg.common.util.nio;

import org.tinylog.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class TCPClient {


    public SocketChannel channel;

    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("localhost", 9999));
        TCPClient tcpClient = new TCPClient();
        tcpClient.channel = socketChannel;
        tcpClient.registChannel();
    }

    public void registChannel() throws IOException {
        Selector selector = Selector.open();
        channel.configureBlocking(false);
        SelectionKey selectionKey = channel.register(selector, SelectionKey.OP_CONNECT | SelectionKey.OP_READ | SelectionKey.OP_WRITE);  //筛选出关注的集合
        int interestSet = selectionKey.interestOps();
        Logger.info("请求连接");
        if ((interestSet & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
            getFile();
        }

        if ((interestSet & SelectionKey.OP_WRITE) == SelectionKey.OP_WRITE) {
            sendFile();
        }

    }

    public void sendFile() throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(10 * 1024);
        File file = new File("D:\\test\\client\\in.txt");
        FileChannel localChannel = new FileInputStream(file).getChannel();
        byteBuffer.clear();
        while (localChannel.read(byteBuffer) != -1) {

        }
        byteBuffer.flip();
        channel.write(byteBuffer);
        localChannel.close();

    }

    public void getFile() throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(10 * 1024);
        File file = new File("D:\\test\\client\\out.txt");
        byteBuffer.clear();
        while (channel.read(byteBuffer) != -1) {

        }
        FileChannel localChannel = new FileOutputStream(file).getChannel();
        byteBuffer.flip();
        localChannel.write(byteBuffer);
        localChannel.close();
    }


}
