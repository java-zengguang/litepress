package com.zg.sse.event;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.zg.sse.entity.ServerEventSourceEntity;
import com.zg.sse.exeception.SSEException;
import org.tinylog.Logger;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AEventSourceAdapter {
    private static Table<String, String, AEventSource> eventSourceTable = HashBasedTable.create();


    public static AEventSource createAEventSource(String groupId, String clientId) {
        AEventSource aEventSource = new AEventSource(groupId, clientId);
        eventSourceTable.put(groupId, clientId, aEventSource);
        return aEventSource;
    }

    static synchronized void removeAEventSource(String groupId, String clientId,String id) {
        AEventSource aEventSource = eventSourceTable.get(groupId, clientId);
        if(aEventSource.id.equals(id)){
            eventSourceTable.remove(groupId, clientId);
        }
    }

    public static AEventSource getAEventSource(String groupId, String clientId) {
        return eventSourceTable.get(groupId, clientId);
    }


    public static void sendEventByGroupId(ServerEventSourceEntity serverEventSourceEntity) throws SSEException {
        Map<String, AEventSource> eventSourceMap = eventSourceTable.row(serverEventSourceEntity.groupId);
        if (eventSourceMap == null || eventSourceMap.size() == 0) {
            throw new SSEException("未找到存活的客户端");
        }
        eventSourceMap.forEach((key, value) -> {
            try {
                value.sendEvent(serverEventSourceEntity.eventType, serverEventSourceEntity.data);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }





    public static void sendEventSource(ServerEventSourceEntity serverEventSourceEntity) throws IOException, SSEException {
        AEventSource aEventSource = eventSourceTable.get(serverEventSourceEntity.groupId, serverEventSourceEntity.clientId);
        if (aEventSource != null) {
            throw new SSEException("未找到存活的SSE客户端");
        }
        aEventSource.sendEvent(serverEventSourceEntity.eventType, serverEventSourceEntity.data);
    }


}
