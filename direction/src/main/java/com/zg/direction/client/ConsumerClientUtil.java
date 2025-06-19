package com.zg.direction.client;

import com.zg.direction.adapter.ProviderRegister;
import com.zg.direction.entity.DTPRequest;
import com.zg.direction.entity.DTPResponse;
import com.zg.direction.entity.ProviderEntity;

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.locks.LockSupport;

public class ConsumerClientUtil {
    public final static Map<String, Thread> synRequestThreadMap = new Hashtable<>();  //消息发送阻塞队列，发送时将现线程放入，返回时唤醒线程

    //异步方法
    public static DTPResponse addASynRequest(String providerName, DTPRequest request) throws Exception {
        //从注册中心获取配置
        ProviderRegister providerRegister = ProviderRegister.getInstance();
        ProviderEntity providerEntity = providerRegister.findPriorityNode(providerName);
        //添加一些配置信息
        request.className = providerEntity.className;
        request.providerName = providerEntity.providerName;
        request.path = providerEntity.path;
        //执行同步方法
        SimpleClient simpleClient = SimpleClient.getInstance(providerEntity.host, providerEntity.port, providerEntity.clientVersion);
        simpleClient.addRequest(request);
        DTPResponse response = new DTPResponse();
        response.id = request.id;
        response.success = true;
        return response;

    }

    //同步返回结果
    public static DTPResponse addSynRequest(String providerName, DTPRequest request) throws Exception {
        //从注册中心获取配置
        ProviderRegister providerRegister = ProviderRegister.getInstance();
        ProviderEntity providerEntity = providerRegister.findPriorityNode(providerName);
        //添加一些配置信息
        request.className = providerEntity.className;
        request.providerName = providerEntity.providerName;
        request.path = providerEntity.path;
        //执行同步方法
        synRequestThreadMap.put(request.id, Thread.currentThread()); //添加线程做同步等待
        SimpleClient simpleClient = SimpleClient.getInstance(providerEntity.host, providerEntity.port, providerEntity.clientVersion);
        simpleClient.addRequest(request);
        LockSupport.parkUntil(System.currentTimeMillis() + 10 * 1000);
        DTPResponse response = (DTPResponse) simpleClient.getResult(request.id);
        if (response == null) {
            response = new DTPResponse();
            response.id = request.id;
            response.success = false;
            response.error = request.id + "没有收到返回消息，可能服务变化" + request.path + "clieckversion" + providerEntity.clientVersion;
        }

        if (response != null && !response.success) {
            throw new Exception(response.error);
        }
        return response;

    }


}
