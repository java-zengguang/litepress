package com.zg.sse.event;


import com.zg.common.util.reflect.JsonUtils;
import org.eclipse.jetty.servlets.EventSource;
import org.tinylog.Logger;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

;

public class AEventSource implements EventSource {

    public final String id;
    private EventSource.Emitter emitter;

    public String groupId;  //分组标识，标识一个客户的或者一类

    private String clientId;  //客户端的标识，用于向指定客户端发送消息

    private Date timestamp=new Date();

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public AEventSource(String groupId, String clientId) {
        this.id= UUID.randomUUID().toString();
        this.groupId = groupId;
        this.clientId = clientId;
    }

    @Override
    public void onOpen(Emitter emitter) throws IOException {
        this.emitter = emitter;
        emitter.data(JsonUtils.objectToJsonString( Map.of("groupId", groupId, "clientId", clientId)));
        // 打开连接时的处理逻辑
        Logger.info("创建事件资源"+id);

    }

    @Override
    public void onClose() {
        // 关闭连接时的处理逻辑
        Logger.info("关闭事件资源"+id);
        AEventSourceAdapter.removeAEventSource(groupId,clientId,id);
    }


    public void sendEvent(String event, String data) throws IOException {
        try {
            emitter.event(event, data);
        }catch (IOException e){
            Logger.error("推送异常，链接不到客户端",e);
        }

    }

    public void close() throws IOException {
        emitter.close();

    }





}
