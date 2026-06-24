package io.github.java_zengguang.litepress.direction.server;


import com.fasterxml.jackson.databind.JsonNode;
import io.github.java_zengguang.litepress.core.util.reflect.JsonUtil;
import io.github.java_zengguang.litepress.direction.entity.DTPRequest;
import io.github.java_zengguang.litepress.direction.entity.DTPResponse;
import io.github.java_zengguang.litepress.network.common.service.NettyServiceHandler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;

import org.tinylog.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@ChannelHandler.Sharable
public class ProviderServiceHandler extends NettyServiceHandler {


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
        if (value instanceof JsonNode || value instanceof Map<?,?>) {
            result = JsonUtil.string2Obj(JsonUtil.obj2String(value),type);
        }
        return result;
    }

    private Object[] getParamters(List<Object> paramterValues, List<Type> paramterTypes)   {
        Object[] result = null;
        result = new Object[paramterTypes.size()];
        for (int i = 0; i < paramterTypes.size(); i++) {
            result[i] = analysisObject(paramterTypes.get(i), paramterValues.get(i));
        }
        return result;
    }


    private String serialize(Object object) {
       return JsonUtil.obj2String(object);
    }

    private Object unSerialize(String str, Class classType) {
        return JsonUtil.string2Obj(str, classType);
    }





    @Override
    public void sendMsg(Channel channel, String msg) throws Exception {

        Logger.debug("get msg >" + msg);

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
            Object[] paramters = getParamters(request.methodParamters, Arrays.asList(paramerTypes));
            //方法体执行开始
            //zookeeper暂用
            String path = request.path;
            String providerName = request.providerName;
            Logger.debug("任务处理provider：" + path);
            LocalDateTime startTime = LocalDateTime.now();
            Object result=null;
            try {
                result = method.invoke(classes.newInstance(), paramters);
            }catch (InvocationTargetException e){
                throw e.getCause();
            }
            LocalDateTime endTime = LocalDateTime.now();
            Logger.debug(path + "任务处理时长：" + Duration.between(startTime, endTime).toMillis());
            //zookeeper释放
            //方法体执行结束
            response.success = true;
            response.resultData =result;
            response.resultType = request.resultType;
            response.resultDataType = request.resultDataType;
        } catch (Throwable e) {
            Logger.error(e);
            response.success = false;
            response.resultData = null;
            response.resultType = request.resultType;
            response.resultDataType = request.resultDataType;
            response.error = e.getMessage();
        }
        response.id = request.id;
        String responseJson = serialize(response);
        channel.writeAndFlush(responseJson + "\r\n");
    }
}
