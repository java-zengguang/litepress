package com.zg.event.driver.subsriber;

import com.zg.event.driver.event.BaseEvent;
import com.zg.event.driver.subsriber.components.EventComponent;

public abstract class BaseComponentEventListener implements EventListener {
    ThreadLocal<BaseEvent> threadLocal = new ThreadLocal<>();


    public BaseEvent getBaseEvent() {
        return threadLocal.get();
    }


    //如果需要处理事件、幂等、等操作可以重写 dealEvent方法
    @Override
    public void dealEvent(BaseEvent event) throws Exception {
        threadLocal.set(event);
        defineComponent(event);
    }

    @Override
    public void callBack(String eventMessage) throws Exception {
        return;
    }

    public abstract void defineComponent(BaseEvent event) throws Exception;


    public void dealComponent(EventComponent eventComponent) throws Exception {
        eventComponent.dealEvent(this.getBaseEvent());
    }
}
