package com.zg.direction.proxy;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zg.direction.client.ConsumerClient;
import com.zg.direction.entity.DTPRequest;
import com.zg.direction.entity.DTPResponse;
import org.apache.zookeeper.KeeperException;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;


public class ConsumerHandler implements InvocationHandler {

    private String synFlag = "0";  //0-同步 1-异步
    private String className;
    private ConsumerClient consumerClient;

    public ConsumerHandler(String providerName, String synFlag) {

        try {
            this.synFlag=synFlag;
            consumerClient = ConsumerClient.getInstance(providerName);
            className = consumerClient.getClassName();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public ConsumerHandler(String providerName) {

        try {

            consumerClient = ConsumerClient.getInstance(providerName);
            className = consumerClient.getClassName();


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
        UUID uuid = UUID.randomUUID();//使用唯一请求标识
        DTPRequest request = new DTPRequest();
        request.id = uuid.toString();
        request.className = className;
        request.methodName = method.getName();
        Map<String, String> returnDataMap = analysisType(method.getGenericReturnType());
        request.resultType = returnDataMap.get("Type");
        request.resultDataType = returnDataMap.get("DataType");

        List methodParamters = new ArrayList<>();
        List<String> methodParamterTypes = new ArrayList<>();
        List<String> methodParamterDataTypes = new ArrayList<>();
        List<Map<String,String>> methodParamterDataTypeMap=new ArrayList<>();
        Type[] paramerTypes = method.getGenericParameterTypes();

        for (int i = 0; i < paramerTypes.length; i++) {
            Object arg = args[i];
            methodParamters.add(arg);
            Map<String, String> methodParamDataMap = analysisType(paramerTypes[i]);
            methodParamterTypes.add(methodParamDataMap.get("Type"));
            methodParamterDataTypes.add(methodParamDataMap.get("DataType"));
            methodParamterDataTypeMap.add(methodParamDataMap);
        }

        request.methodParamterTypes = methodParamterTypes;
        request.methodParamters = methodParamters;
        request.methodParamterDataTypes = methodParamterDataTypes;
        request.methodParamterDataTypeMapList =methodParamterDataTypeMap;
        request.providerName=consumerClient.getProviderEntity().providerName;
        request.path=consumerClient.getProviderEntity().path;

        DTPResponse response ;
        if("0".equals(synFlag)){
            response =  consumerClient.addSynRequest(request);
        }else{
             response =  consumerClient.addASynRequest(request);
        }
        if(response==null){
            response=new DTPResponse();
            response.id=request.id;
            response.success=false;
            response.error=request.id+"没有收到返回消息，可能服务变化"+request.path+"clieckversion"+consumerClient.getProviderEntity().clientVersion;
        }
        if (response!=null&&!response.success) {
            throw new Exception(response.error);
        }
        Object result = null;
        if (!"".equals(response.resultType) && !"NULL".equals(response.resultType)) {
            result = response.resultData;
        }
        if (result != null) {
            result = analysisObject(method.getGenericReturnType(), result);
        }

        return result;
    }
}
