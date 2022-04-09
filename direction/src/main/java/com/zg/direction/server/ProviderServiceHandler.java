package com.zg.direction.server;

import com.zg.direction.entity.DTPRequest;
import com.zg.direction.entity.DTPResponse;
import com.zg.direction.entity.ParamterEntity;
import com.zg.network.common.service.BaseServiceHandler;
import com.zg.util.reflect.EntityUtils;
import com.zg.util.reflect.JsonUtils;
import io.netty.channel.ChannelHandlerContext;

import java.lang.reflect.Method;
import java.util.List;

public class ProviderServiceHandler extends BaseServiceHandler<String> {


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
            result[i] = Class.forName(paramterTypes.get(i));
        }

        return result;
    }

    private Object[] getParamters(List<String> paramterValues, Class[] paramterTypes) {
        Object[] result = null;


        result = new Object[paramterTypes.length];

        for (int i = 0; i < paramterTypes.length; i++) {
            if (EntityUtils.isPrimitive(paramterTypes[i])) {
                result[i] = EntityUtils.translateType(paramterValues.get(i), paramterTypes[i]);
            }
        }

        return result;

    }


    private String serialize(Object object) throws IllegalAccessException {
        String data = EntityUtils.serialize(object);
        return data;
    }

    private Object unSerialize(String str, Class classType) throws IllegalAccessException, InstantiationException {
        Object object = EntityUtils.unSerialize(str, classType);
        return object;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws IllegalAccessException {

        System.out.println("get msg >" + msg);

        DTPRequest request = null;
        DTPResponse response = new DTPResponse();
        try {
            request = (DTPRequest) unSerialize(msg, DTPRequest.class);

            String className = request.className;
            String methodName = request.methodName;
            String uuid = request.uuid;
            String token = request.token;

            //     BaseChannelGroups.put(uuid, token, ctx.channel());

            Class[] paramterTypes = getParamterTypes(request.methodParamterTypes);
            Class classes = Class.forName(className);
            Method method = classes.getDeclaredMethod(methodName, paramterTypes);
            Object paramters[] = getParamters(request.methodParamters, method.getParameterTypes());
            Object result = method.invoke(classes.newInstance(), paramters);
            response.success = true;
            response.resultData = serialize(result);
            response.resultType = request.methodType;
        } catch (Exception e) {
            e.printStackTrace();
            response.success = false;
            response.resultData = null;
            response.resultType = request.methodType;
            response.error = e.getMessage();
        }

        //String responseJson=JsonUtils.objectToJson(response).toString();
        String responseJson = serialize(response);
        // Channel channel=BaseChannelGroups.getChannel("");
        // channel.writeAndFlush(responseJson+"\r\n");
        ctx.writeAndFlush(responseJson + "\r\n");

    }


}
