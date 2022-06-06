package com.zg.direction.proxy;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zg.common.init.Config;
import com.zg.common.util.reflect.JsonUtils;
import com.zg.direction.client.ConsumerClient;
import com.zg.direction.client.ConsumerClientHandler;
import com.zg.direction.entity.DTPRequest;
import com.zg.direction.entity.ProviderConfig;
import com.zg.direction.entity.ProviderEntity;
import com.zg.direction.register.Register;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        } catch (QuorumPeerConfig.ConfigException e) {
            e.printStackTrace();
        }
    }

    private void getClassName() throws IOException, KeeperException, InterruptedException, InstantiationException, IllegalAccessException, QuorumPeerConfig.ConfigException {
        Register register = new Register(providerConfig.registerURL);
        String json = register.findNode(providerName);
        ProviderEntity providerEntity = (ProviderEntity) JsonUtils.jsonToObject(json, ProviderEntity.class);
        this.host = providerEntity.host;
        this.port = providerEntity.port;
        this.className = providerEntity.className;
    }


    Map<String, String> analysisType(Type type) {
        Map map = new HashMap();
        if (type instanceof ParameterizedType) {   //处理集合类泛型
            String typeName = type.getTypeName();
            if (typeName.contains("<")) {
                map.put("Type", typeName.substring(0, typeName.indexOf("<")));
            } else {
                map.put("Type", typeName);
            }
            if (typeName.contains("<")) {
                map.put("DataType", typeName.substring(typeName.indexOf("<") + 1, typeName.lastIndexOf(">")));
            } else {
                map.put("DataType", typeName);
            }
        } else {
            map.put("Type", type.getTypeName());
            map.put("DataType", type.getTypeName());
        }
        return map;
    }

    public Object analysisObject(Type type, Object value) throws ClassNotFoundException {
        Object result = null;
        result = value;
        if (value instanceof JSONObject) {
            result = ((JSONObject) value).toJavaObject(type);
        } else if (value instanceof JSONArray) {
            result=((JSONArray) value).toJavaObject(type);
        }
        return result;
    }



    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        DTPRequest request = new DTPRequest();
        request.className = className;
        request.methodName = method.getName();
        Map<String,String> returnDataMap= analysisType(method.getGenericReturnType());
        request.resultType = returnDataMap.get("Type");
        request.resultDataType = returnDataMap.get("DataType");

        List methodParamters = new ArrayList<>();
        List<String> methodParamterTypes = new ArrayList<>();
        List<String> methodParamterDataTypes = new ArrayList<>();
        Type[] paramerTypes = method.getGenericParameterTypes();

        for (int i = 0; i < paramerTypes.length; i++) {
            Object arg = args[i];
            methodParamters.add(arg);
            Map<String,String> methodParamDataMap= analysisType(paramerTypes[i]);
            methodParamterTypes.add(methodParamDataMap.get("Type"));
            methodParamterDataTypes.add(methodParamDataMap.get("DataType"));
        }

        request.methodParamterTypes = methodParamterTypes;
        request.methodParamters = methodParamters;
        request.methodParamterDataTypes = methodParamterDataTypes;
        ConsumerClientHandler consumerClientHandler = new ConsumerClientHandler();
        ConsumerClient consumerClient = new ConsumerClient(consumerClientHandler, host, port);
        consumerClient.addRequest(request);
        Thread thread = new Thread(consumerClient);
        thread.start();
        Object result = consumerClientHandler.getResult();
        result= analysisObject(method.getGenericReturnType(),result);
        return result;
    }
}
