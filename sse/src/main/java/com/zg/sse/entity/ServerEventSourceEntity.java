package com.zg.sse.entity;

public class ServerEventSourceEntity {

    public String clientId;
    public String groupId;
    public String eventType;
    public String data;

    public ServerEventSourceEntity(String clientId, String groupId, String eventType, String data) {
        this.clientId = clientId;
        this.groupId = groupId;
        this.eventType = eventType;
        this.data = data;
    }
}
