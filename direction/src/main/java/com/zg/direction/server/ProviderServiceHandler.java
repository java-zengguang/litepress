package com.zg.direction.server;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zg.common.util.reflect.EntityUtils;
import com.zg.common.util.reflect.JsonUtils;
import com.zg.direction.entity.DTPRequest;
import com.zg.direction.entity.DTPResponse;
import com.zg.direction.entity.ParamterEntity;
import com.zg.network.common.service.BaseServiceHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

public class ProviderServiceHandler extends BaseServiceHandler<String> {
    public final Logger logger = LoggerFactory.getLogger(this.getClass().getName());


    private Object[] getParamters(List<ParamterEntity> paramterEntityList) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Object[] objects = new Object[paramterEntityList.size()];
        for (int i = 0; i < paramterEntityList.size(); i++) {
            ParamterEntity paramterEntity = paramterEntityList.get(i);
            objects[i] = JsonUtils.jsonToObject(paramterEntity.paramterValue, Class.forName(paramterEntity.paramterType));

        }
        return objects;
    }

    public Class[] getParamterTypes(List<String> paramterTypes) throws ClassNotFoundException {
        Class[] result = null;
        result = new Class[paramterTypes.size()];
        for (int i = 0; i < result.length; i++) {
            String paramterType = paramterTypes.get(i); //取泛型类型
            result[i] = Class.forName(paramterType);
        }

        return result;
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

    private Object[] getParamters(List<Object> paramterValues, List<Type> paramterTypes) throws ClassNotFoundException {
        Object[] result = null;
        result = new Object[paramterTypes.size()];
        for (int i = 0; i < paramterTypes.size(); i++) {
            result[i] = analysisObject(paramterTypes.get(i), paramterValues.get(i));
        }
        return result;
    }


    private String serialize(Object object) {
        String data = EntityUtils.serialize(object);
        return data;
    }

    private Object unSerialize(String str, Class classType) {
        Object object = EntityUtils.unSerialize(str, classType);
        return object;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws IllegalAccessException {

        logger.info("get msg >" + msg);

        DTPRequest request = null;
        DTPResponse response = new DTPResponse();
        try {
            request = (DTPRequest) unSerialize(msg, DTPRequest.class);

            String className = request.className;
            String methodName = request.methodName;
            String uuid = request.uuid;
            String token = request.token;


            Class[] paramterTypes = getParamterTypes(request.methodParamterTypes);
            Class classes = Class.forName(className);
            Method method = classes.getDeclaredMethod(methodName, paramterTypes);
            Type[] paramerTypes = method.getGenericParameterTypes();
            Object paramters[] = getParamters(request.methodParamters, Arrays.asList(paramerTypes));
            Object result = method.invoke(classes.newInstance(), paramters);
            response.success = true;
            response.resultData = result;
            response.resultType = request.resultType;
            response.resultDataType = request.resultDataType;
        } catch (Exception e) {
            e.printStackTrace();
            response.success = false;
            response.resultData = null;
            response.resultType = request.resultType;
            response.resultDataType = request.resultDataType;
            response.error = e.getMessage();
        }

        String responseJson = serialize(response);
        ctx.writeAndFlush(responseJson + "\r\n");

    }


}
