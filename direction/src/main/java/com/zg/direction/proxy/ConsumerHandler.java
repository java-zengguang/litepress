package com.zg.direction.proxy;


import com.fasterxml.jackson.databind.JsonNode;
import com.zg.common.util.reflect.JsonUtil;
import com.zg.direction.client.ConsumerKeepClientUtil;
import com.zg.direction.entity.DTPRequest;
import com.zg.direction.entity.DTPResponse;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;


public class ConsumerHandler implements InvocationHandler {


    private final String providerName;


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
        response = ConsumerKeepClientUtil.addSynRequest(request.providerName, request);


        Object result = null;
        if (!"".equals(response.resultType) && !"NULL".equals(response.resultType)) {
            Object resultData = response.resultData;
            if(resultData instanceof JsonNode || resultData instanceof Map<?,?> || resultData instanceof List<?>) {
                result = JsonUtil.string2Obj(JsonUtil.obj2String(resultData), method.getGenericReturnType());
            }else{
                result=resultData;
            }
        }

        return result;
    }
}
