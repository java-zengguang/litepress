package com.zg.event.driver.bus;



import com.zg.event.driver.event.BaseEvent;
import com.zg.event.driver.event.manager.EventStateManager;
import com.zg.event.driver.event.rule.EventTransitionRule;
import com.zg.event.driver.exception.StateTransitinException;
import com.zg.event.driver.subsriber.EventListener;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.io.IOException;

public interface MessageBus {

    //发布
    void publish(BaseEvent baseEvent) throws IOException, MQBrokerException, RemotingException, InterruptedException, MQClientException, StateTransitinException;

    void subscriber(String eventType, EventListener listener) throws MQClientException, InterruptedException;


    void setEventStateManager(EventStateManager eventStateManager);

    void subscriber(String eventType, String eventStage, EventListener listener, EventTransitionRule eventTransitionRule) throws MQClientException, InterruptedException;

}
