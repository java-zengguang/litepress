package io.github.java_zengguang.litepress.event.bus;



import io.github.java_zengguang.litepress.event.event.BaseEvent;
import io.github.java_zengguang.litepress.event.event.manager.EventStateManager;
import io.github.java_zengguang.litepress.event.event.rule.EventTransitionRule;
import io.github.java_zengguang.litepress.event.exception.StateTransitinException;
import io.github.java_zengguang.litepress.event.subsriber.EventListener;
import io.github.java_zengguang.litepress.react.semaphore.SemaphoreManager;
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
