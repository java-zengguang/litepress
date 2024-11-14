package com.zg.event.driver;

import com.zg.event.driver.bus.BaseZeroMQBus;
import com.zg.event.driver.entity.ZoreMQConfig;
import com.zg.event.driver.event.BaseEvent;
import com.zg.event.driver.exception.StateTransitinException;
import com.zg.event.driver.subsriber.BaseEventListener;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.io.IOException;

public class Test0 {

    public static void main(String args[]) throws InterruptedException, MQClientException, StateTransitinException, MQBrokerException, RemotingException, IOException {
        BaseZeroMQBus bus=new BaseZeroMQBus(new ZoreMQConfig("5555"));
        bus.init();
        bus.subscriber("hello", new BaseEventListener() {
            @Override
            public void callBack(String eventMessage) throws Exception {
                System.out.println("hello "+eventMessage);
            }
        });



        bus.publish(new BaseEvent("hello","zengguang"));
        bus.publish(new BaseEvent("hello","zengguang"));
        bus.publish(new BaseEvent("1","zengguang"));


        Thread.sleep(10000);
    }
}
