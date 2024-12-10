package com.zg.event.driver.bus;


import com.zg.event.driver.entity.RocketConfig;
import com.zg.event.driver.event.BaseEvent;
import org.apache.rocketmq.client.exception.MQClientException;

public class RocketMQBus extends BaseRocketMQBus {


    public RocketMQBus(RocketConfig rocketConfig) throws InterruptedException, MQClientException {
        super(rocketConfig);
    }



}
