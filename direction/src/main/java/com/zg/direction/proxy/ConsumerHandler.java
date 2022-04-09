package com.zg.direction.proxy;

import com.zg.direction.client.ConsumerClient;
import com.zg.direction.client.ConsumerClientHandler;
import com.zg.direction.entity.DTPRequest;
import com.zg.direction.entity.ProviderConfig;
import com.zg.direction.entity.ProviderEntity;
import com.zg.direction.register.ZookeeperUtil;
import com.zg.init.Config;
import com.zg.util.reflect.JsonUtils;
import org.apache.zookeeper.KeeperException;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ConsumerHandler implements InvocationHandler {


    private ProviderConfig providerConfig = (ProviderConfig) Config.getConfig("providerConfig");

    private String providerName;

    private String host;

    private int port;

    private String className;

    public ConsumerHandler(String providerName) {
        this.providerName = providerName;
        try {
            getClassName();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void getClassName() throws IOException, KeeperException, InterruptedException, InstantiationException, IllegalAccessException {
        ZookeeperUtil zookeeperUtil = new ZookeeperUtil(providerConfig.registerURL);
        String json = zookeeperUtil.findNode(providerName);
        ProviderEntity providerEntity = (ProviderEntity) JsonUtils.jsonToObject(json, ProviderEntity.class);
        this.host = providerEntity.host;
        this.port = providerEntity.port;
        this.className = providerEntity.className;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        DTPRequest request = new DTPRequest();
        request.className = className;
        request.methodName = method.getName();
        request.methodType = method.getReturnType().getName();
        List methodParamters = new ArrayList<>();
        List<String> methodParamterTypes = new ArrayList<>();
        for (Object arg : args) {

            Class classes = arg.getClass();
            methodParamters.add(arg);
            methodParamterTypes.add(classes.getName());
        }
        request.methodParamterTypes = methodParamterTypes;
        request.methodParamters = methodParamters;
        ConsumerClientHandler consumerClientHandler = new ConsumerClientHandler();
        ConsumerClient consumerClient = new ConsumerClient(consumerClientHandler, host, port);
        consumerClient.addRequest(request);
        Thread thread = new Thread(consumerClient);
        thread.start();
        Object result = consumerClientHandler.getResult();
        return result;
    }
}
