package com.zg.event.driver.event;

import com.zg.event.driver.en.ProcessState;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.*;

public class BaseEvent {

    public String eventMethod;   // BATCH 批量  ONE 单条
    public String eventID;  //事件ID，结合redis 保证 10分钟内
    public String eventType; //事件类型标识

    public String message;//事件消息

    public String processStage;  //流程阶段  默认 0 ,流程初始状态

    public String processState;  //流程状态  默认 0-初始化  1-正常完成  2-流程进行中  3-异常终止


    public String eventStage;  //事件节点 init-初始化（排队中）   successful-成功  failure-失败   progress-进行中  retry-重试

    public String businessNo;

    public String processID;


    public String result;  //当前事件的处理结果

    public String errorMessage;


    public Map<String,Object> paramMap=new HashMap<>();

    public Map<String,String> traceMap=new HashMap<>();

    public List<String> eventTrace=new ArrayList<>();




    public BaseEvent() {
    }

    public BaseEvent(String eventType, String message) {
        this.eventType = eventType;
        this.message = message;
        this.businessNo = UUID.randomUUID().toString();
        this.eventID = eventType + "_" + businessNo;
        this.processStage ="0";
        this.processState= ProcessState.INIT.name();
        this.processID="EVENT_TRACE_"+ DateFormatUtils.format(new Date(),"yyyyMM");

    }

    public BaseEvent(String eventType, String businessNo, String message) {
        this.eventType = eventType;
        this.message = message;
        this.businessNo = businessNo;
        this.eventID = eventType + "_" + businessNo;
        this.processStage ="0";
        this.processState= ProcessState.INIT.name();
        this.processID="EVENT_TRACE_"+ DateFormatUtils.format(new Date(),"yyyyMM");

    }


    public BaseEvent(String eventType, String businessNo, String processStage, String message) {
        this.eventType = eventType;
        this.message = message;
        this.businessNo = businessNo;
        this.eventID = eventType + "_" + businessNo;
        this.processStage = processStage;
        this.processState= ProcessState.INIT.name();
        this.processID="EVENT_TRACE_"+ DateFormatUtils.format(new Date(),"yyyyMM");

    }


}
