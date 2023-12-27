package com.zg.direction.client;

import com.zg.common.util.reflect.EntityUtils;
import com.zg.direction.listener.SimpleReceivedListener;
import com.zg.network.common.client.BaseClient;

import java.util.Hashtable;
import java.util.Map;

public class SimpleClient extends BaseClient {

    private static Map<String, SimpleClient> clientMap = new Hashtable<>();
    public String state = "0";// 0-初始化  1-执行完成  3-正在执行 2-执行出错

    public SimpleReceivedListener simpleReceivedListener;
    public Thread thread;//守护线程

    private SimpleClient(String host, int port) {
        // ConsumerClientHandler clientHandler = new ConsumerClientHandler();
        SimpleClientHandler clientHandler = new SimpleClientHandler();  //创建消息处理类
        simpleReceivedListener = new SimpleReceivedListener();  //消息返回处理监听
        clientHandler.addMessgeReceivedListener(simpleReceivedListener);
        initParam(clientHandler, host, port);
    }

    public static synchronized SimpleClient getInstance(String host, int port, String clientVersion) {
        String address = host + ":" + port + ":" + clientVersion;
        SimpleClient simpleClient = clientMap.get(address);
        if (simpleClient == null  ) {
            simpleClient = new SimpleClient(host, port);
            simpleClient.state = "3";
            clientMap.put(address, simpleClient);
            simpleClient.doStart();
        }

        return simpleClient;

    }

    private void doStart() {
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }


    public Object getResult(String id) {
        return simpleReceivedListener.getResult(id);
    }


    @Override
    public String resovleProtocol(Object object) {
        String json = null;
        json = EntityUtils.serialize(object);
        return json;
    }

}
