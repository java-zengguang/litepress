package io.github.java_zengguang.litepress.event.event;

import io.github.java_zengguang.litepress.event.state.po.EventInstancePo;

import java.util.ArrayList;
import java.util.List;

public class BaseEvent {
    public String id;  //事件ID，结合redis 保证 10分钟内
    public String parentId;  //父事件ID
    public String name; //事件类型标识
    public String message;//事件消息
    public EventInstancePo instance;
    public List<BaseEvent> nextEvents=new ArrayList<>();
}
