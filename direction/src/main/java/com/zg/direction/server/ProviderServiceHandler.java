package com.zg.direction.server;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zg.common.util.reflect.EntityUtils;
import com.zg.direction.entity.DTPRequest;
import com.zg.direction.entity.DTPResponse;
import com.zg.network.common.service.BaseServiceHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import org.tinylog.Logger;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@ChannelHandler.Sharable
public class ProviderServiceHandler extends BaseServiceHandler<String> {


/*    private Object[] getParamters(List<ParamterEntity> paramterEntityList) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Object[] objects = new Object[paramterEntityList.size()];
        for (int i = 0; i < paramterEntityList.size(); i++) {
            ParamterEntity paramterEntity = paramterEntityList.get(i);
            objects[i] = JsonUtils.jsonToObject(paramterEntity.paramterValue, Class.forName(paramterEntity.paramterType));

        }
        return objects;
    }*/

    public Class[] getParamterTypes(List<String> paramterTypes) throws ClassNotFoundException {
        Class[] result = new Class[paramterTypes.size()];
        for (int i = 0; i < result.length; i++) {
            String paramterType = paramterTypes.get(i); //取泛型类型
            result[i] = Class.forName(paramterType);
        }

        return result;
    }

    public Object analysisObject(Type type, Object value) {
        Object result = value;
        if (value instanceof JSONObject) {
            result = ((JSONObject) value).toJavaObject(type);
        } else if (value instanceof JSONArray) {
            result = ((JSONArray) value).toJavaObject(type);
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
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {

        Logger.info("get msg >" + msg);

        DTPRequest request = null;
        DTPResponse response = new DTPResponse();
        try {
            request = (DTPRequest) unSerialize(msg, DTPRequest.class);

            String className = request.className;
            String methodName = request.methodName;
            String uuid = request.uuid;
            String token = request.token;

            List<String> methodParamterTypes = request.methodParamterTypes;
            Class[] paramterTypes = getParamterTypes(methodParamterTypes);
            Class classes = Class.forName(className);
            Method method = classes.getDeclaredMethod(methodName, paramterTypes);
            Type[] paramerTypes = method.getGenericParameterTypes();
            Object paramters[] = getParamters(request.methodParamters, Arrays.asList(paramerTypes));
            //方法体执行开始
            //zookeeper暂用
            String path = request.path;
            String providerName = request.providerName;
            Logger.info("任务处理provider：" + path);
            LocalDateTime startTime = LocalDateTime.now();
            Object result = method.invoke(classes.newInstance(), paramters);
            LocalDateTime endTime = LocalDateTime.now();
            Logger.info(path + "任务处理时长：" + Duration.between(startTime, endTime).toMillis());
            //zookeeper释放
            //方法体执行结束
            response.success = true;
            response.resultData = result;
            response.resultType = request.resultType;
            response.resultDataType = request.resultDataType;
        } catch (Exception e) {
            Logger.error(e);
            response.success = false;
            response.resultData = null;
            response.resultType = request.resultType;
            response.resultDataType = request.resultDataType;
            response.error = e.getMessage();
        }
        response.id = request.id;
        String responseJson = serialize(response);
        ctx.writeAndFlush(responseJson + "\r\n");

    }


}
