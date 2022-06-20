package com.zg.direction.proxy;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zg.common.init.Config;
import com.zg.common.util.reflect.JsonUtils;
import com.zg.direction.client.ConsumerClient;
import com.zg.direction.entity.DTPRequest;
import com.zg.direction.entity.DTPResponse;
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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ConsumerHandler implements InvocationHandler {

    private static Map<String, ConsumerClient> clientMap = new ConcurrentHashMap<>();
    private ProviderConfig providerConfig = (ProviderConfig) Config.getConfig("providerConfig");

    private String providerName;

    private String className;
    private ConsumerClient consumerClient;

    public ConsumerHandler(String providerName) {
        this.providerName = providerName;
        try {

            consumerClient=clientMap.get(providerName);
            if (consumerClient==null) {
                consumerClient=new ConsumerClient(providerName);
                clientMap.put(providerName,consumerClient);
                consumerClient.doStart();
            }

            className=consumerClient.getClassName();


        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
            result = ((JSONArray) value).toJavaObject(type);
        }
        return result;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        DTPRequest request = new DTPRequest();
        request.id = "" + (new Date()).getTime();
        request.className = className;
        request.methodName = method.getName();
        Map<String, String> returnDataMap = analysisType(method.getGenericReturnType());
        request.resultType = returnDataMap.get("Type");
        request.resultDataType = returnDataMap.get("DataType");

        List methodParamters = new ArrayList<>();
        List<String> methodParamterTypes = new ArrayList<>();
        List<String> methodParamterDataTypes = new ArrayList<>();
        Type[] paramerTypes = method.getGenericParameterTypes();

        for (int i = 0; i < paramerTypes.length; i++) {
            Object arg = args[i];
            methodParamters.add(arg);
            Map<String, String> methodParamDataMap = analysisType(paramerTypes[i]);
            methodParamterTypes.add(methodParamDataMap.get("Type"));
            methodParamterDataTypes.add(methodParamDataMap.get("DataType"));
        }

        request.methodParamterTypes = methodParamterTypes;
        request.methodParamters = methodParamters;
        request.methodParamterDataTypes = methodParamterDataTypes;
        consumerClient.addRequest(request);

        DTPResponse response = null;
        int maxWait = 10000;
        int oneWait = 50;
        int currentWait = 0;
        Object result = null;
        do {
            Thread.sleep(oneWait);
            response = (DTPResponse) consumerClient.getResult(request.id);
            currentWait = currentWait + oneWait;
            if (currentWait > maxWait) {
                throw new Exception("请求超时");
            }
        } while (response == null);
        if (!response.success) {
            throw new Exception(response.error);
        }
        if (!"".equals(response.resultType) && !"NULL".equals(response.resultType)) {
            result = response.resultData;
        }
        if (result != null) {
            result = analysisObject(method.getGenericReturnType(), result);
        }
        return result;
    }
}
