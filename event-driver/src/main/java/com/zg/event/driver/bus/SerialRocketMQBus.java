package com.zg.event.driver.bus;


import com.zg.event.driver.entity.RocketConfig;
import org.apache.rocketmq.client.exception.MQClientException;

public class SerialRocketMQBus extends BaseRocketMQBus {


    public SerialRocketMQBus(RocketConfig rocketConfig) throws InterruptedException, MQClientException {
        super(rocketConfig);
    }



}
