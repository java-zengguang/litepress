package com.zg.event.driver.event.rule;


import com.zg.event.driver.en.ProcessState;
import com.zg.event.driver.event.BaseEvent;
import com.zg.event.driver.event.action.StateAction;
import com.zg.event.driver.event.action.StateHandler;
import com.zg.event.driver.exception.StateTransitinException;

public abstract class BaseEventTransitionRule implements EventTransitionRule {

    public String form;
    public String event;
    public StateHandler when;
    public String to;

    public StateAction action;

    public String nextEvent;  //发送，下一个事件

    public boolean end=false;  //终止节点，标识流程最终状态，默认false




    @Override
    public boolean checkTransitionRule(BaseEvent baseEvent)  {
        //初始状态
        if (!baseEvent.processStage.equals(this.form)) {
            return false;
        }
        //事件
        if (this.event != null && !baseEvent.eventType.equals(this.event)) {
            return false;
        }
        //条件
        if (this.when != null && !this.when.deal(baseEvent)) {
            return false;
        }
        return true;
    }
    @Override
    public void doTransitionState(BaseEvent baseEvent){
        if (this.to != null) {
            baseEvent.processStage = this.to;  //转换状态
            if(end){
                baseEvent.processState= ProcessState.SUCCESSFUL.name();  //最终节点
            }
        }
    }

    @Override
    public  void doAction(BaseEvent baseEvent) throws StateTransitinException {
        if (action != null) {
            try {
                action.deal(baseEvent);
            } catch (Exception e) {
                throw new StateTransitinException("动作执行失败");
            }
        }
    }
    @Override
    public String getNextEvent(){
        return nextEvent;
    }

}
