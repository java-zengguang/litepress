package com.zg.litepress.event.bus;


import com.zg.litepress.event.entity.RocketConfig;
import org.apache.rocketmq.client.exception.MQClientException;

public class RocketMQBus extends BaseRocketMQBus {


    public RocketMQBus(RocketConfig rocketConfig) throws InterruptedException, MQClientException {
        super(rocketConfig);
    }



}
