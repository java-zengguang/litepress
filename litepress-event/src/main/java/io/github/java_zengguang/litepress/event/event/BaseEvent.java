package io.github.java_zengguang.litepress.event.event;

import io.github.java_zengguang.litepress.event.en.ProcessState;
import io.github.java_zengguang.litepress.event.state.po.StatePo;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public  class BaseEvent {

    public String eventMethod;   // BATCH 批量  ONE 单条
    public String eventID;  //事件ID，结合redis 保证 10分钟内
    public String eventType; //事件类型标识
    public String message;//事件消息
    public String eventState;  //事件的业务状态
    public String processState;  //流程状态  异常

    public String result;  //当前事件的处理结果
    public String errorMessage;

    public StatePo statePo;



    public Map<String, Object> paramMap = new HashMap<>();

    public Map<String, String> traceMap = new HashMap<>();




    public BaseEvent() {
    }


    public BaseEvent(String eventType, String message) {
        this.eventType = eventType;
        this.message = message;
        this.eventID = UUID.randomUUID().toString();
        this.processState = ProcessState.INIT.name();
    }

    public BaseEvent(String eventType,String stateModelId, String message) {
        this.eventType = eventType;
        this.statePo = new StatePo(stateModelId);
        this.message = message;
        this.eventID = UUID.randomUUID().toString();
        this.processState = ProcessState.INIT.name();
    }

}
