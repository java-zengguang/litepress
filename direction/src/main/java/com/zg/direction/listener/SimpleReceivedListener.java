package com.zg.direction.listener;

import com.zg.common.util.reflect.EntityUtils;
import com.zg.direction.client.ConsumerClientUtil;
import com.zg.direction.entity.DTPResponse;
import com.zg.network.common.MessgeReceivedListener;

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.locks.LockSupport;


//用于处理消息返回
public class SimpleReceivedListener implements MessgeReceivedListener {
    private Map<String, Object> resultMap = new Hashtable<>();

    private Object unSerialize(String str, Class classType) {
        Object object = EntityUtils.unSerialize(str, classType);
        return object;
    }

    @Override
    public  void onMessageReceived(Object response) {

        DTPResponse dtpResponse = (DTPResponse) unSerialize((String) response, DTPResponse.class);
        if (response != null) {
            resultMap.put(dtpResponse.id, dtpResponse);
            Thread thread= ConsumerClientUtil.synRequestThreadMap.remove(dtpResponse.id);
            if(thread!=null){  //是空的说明走异步，不需要返回消息
                LockSupport.unpark(thread);  //唤醒线程
            }
        }

    }

    @Override
    public void onMessageDisconnect() {

    }

    @Override
    public void onMessageConnect() {

    }


    public  Object getResult(String id)   {
        DTPResponse response = (DTPResponse) resultMap.remove(id);
        return response;
    }
}
