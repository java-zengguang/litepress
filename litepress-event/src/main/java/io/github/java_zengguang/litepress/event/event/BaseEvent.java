package io.github.java_zengguang.litepress.event.event;

import io.github.java_zengguang.litepress.event.state.po.EventInstancePo;

import java.util.HashMap;
import java.util.Map;

public class BaseEvent {
    public String id;  //事件ID，结合redis 保证 10分钟内
    public String parentId; //父事件ID
    public String instanceId;
    public EventInstancePo instance;
    public String name; //事件类型标识
    public String message;//事件消息
    public String errorMessage;
    public String result;  //当前事件的处理结果

    public Map<String, Object> paramMap = new HashMap<>();

    public Map<String, String> traceMap = new HashMap<>();



}
