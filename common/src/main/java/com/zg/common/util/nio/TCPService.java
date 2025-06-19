package com.zg.common.util.nio;

import org.tinylog.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class TCPService {

    public volatile AtomicBoolean isOpen = new AtomicBoolean(true);
    public ServerSocketChannel serviceChannel;

    public static void main(String args[]) throws IOException {

        TCPService tcpService = new TCPService();
        ServerSocketChannel serviceChannel = ServerSocketChannel.open();
        serviceChannel.bind(new InetSocketAddress(9999));
        tcpService.serviceChannel = serviceChannel;
        tcpService.registChannel();
    }

    public void registChannel() throws IOException {
        Selector selector = Selector.open();

        while (isOpen.get()) {
            SocketChannel channel = serviceChannel.accept();
            channel.configureBlocking(false);
            SelectionKey selectionKey = channel.register(selector, SelectionKey.OP_CONNECT | SelectionKey.OP_READ | SelectionKey.OP_WRITE);  //筛选出关注的集合
            Logger.info(channel.getLocalAddress() + ":连接成功");
            int interestSet = selectionKey.interestOps();
            if ((interestSet & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
                getFile(channel);
            }

            if ((interestSet & SelectionKey.OP_WRITE) == SelectionKey.OP_WRITE) {
                sendFile(channel);
            }
        }


    }

    public void sendFile(SocketChannel channel) throws IOException {

   /*     File file=new File("D:\\test\\in.text");
        FileChannel localChannel=new FileInputStream(file).getChannel();
        localChannel.read(byteBuffer);
        byteBuffer.flip();
        channel.write(byteBuffer);*/

    }

    public void getFile(SocketChannel channel) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(10 * 1024);
        File file = new File("D:\\test\\service\\out.txt");
        FileChannel localChannel = new FileOutputStream(file).getChannel();
        byteBuffer.clear();
        while (channel.read(byteBuffer) != -1) {

        }
        byteBuffer.flip();
        localChannel.write(byteBuffer);
        localChannel.close();

    }

}
