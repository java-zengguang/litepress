package com.zg.litepress.event.bus;



import com.zg.litepress.event.event.BaseEvent;
import com.zg.litepress.event.event.manager.EventStateManager;
import com.zg.litepress.event.event.rule.EventTransitionRule;
import com.zg.litepress.event.exception.StateTransitinException;
import com.zg.litepress.event.subsriber.EventListener;
import com.zg.litepress.react.semaphore.SemaphoreManager;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.io.IOException;

public interface MessageBus {

    //发布
    void publish(BaseEvent baseEvent) throws IOException, MQBrokerException, RemotingException, InterruptedException, MQClientException, StateTransitinException;

    void subscriber(String eventType, EventListener listener) throws MQClientException, InterruptedException;


    void setEventStateManager(EventStateManager eventStateManager);

    void setSemaphoreManager(SemaphoreManager semaphoreManager);

    void subscriber(String eventType, String eventStage, EventListener listener, EventTransitionRule eventTransitionRule) throws MQClientException, InterruptedException;

}
