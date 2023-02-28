package com.zg.direction.proxy;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zg.direction.client.ConsumerClientUtil;
import com.zg.direction.entity.DTPRequest;
import com.zg.direction.entity.DTPResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;


public class ConsumerHandler implements InvocationHandler {

    private String synFlag = "0";  //0-同步 1-异步

    private String providerName;

    public ConsumerHandler( String providerName,String synFlag) {
        this.synFlag = synFlag;
        this.providerName = providerName;
    }

    public ConsumerHandler(String providerName) {
        this.providerName = providerName;
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

    public Object analysisObject(Type type, Object value) {
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
        request.methodName = method.getName();
        Map<String, String> returnDataMap = analysisType(method.getGenericReturnType());
        request.resultType = returnDataMap.get("Type");
        request.resultDataType = returnDataMap.get("DataType");
        request.providerName = providerName;

        List methodParamters = new ArrayList<>();
        List<String> methodParamterTypes = new ArrayList<>();
        List<String> methodParamterDataTypes = new ArrayList<>();
        List<Map<String, String>> methodParamterDataTypeMap = new ArrayList<>();
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
        request.methodParamterDataTypeMapList = methodParamterDataTypeMap;


        DTPResponse response;
        if ("0".equals(synFlag)) {
            response = ConsumerClientUtil.addSynRequest(request.providerName, request);
        } else {
            response = ConsumerClientUtil.addASynRequest(request.providerName, request);
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
