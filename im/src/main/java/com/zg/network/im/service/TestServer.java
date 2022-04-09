package com.zg.network.im.service;

public class TestServer {
    public static void main(String args[]) {
        Thread thread = new Thread(new IMService(new IMServerHandle(), 10000));
        thread.start();
    }
}
